package com.meetupinthemiddle.services
import com.google.maps.PendingResult

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Future

import static java.util.concurrent.Executors.newCachedThreadPool
/**
 * Extension of AbstractGoogleMapsService which also executes its requests concurrently.
 */
class AbstractConcurrentGoogleMapsService<T, R extends PendingResult<T>> extends AbstractGoogleMapsService<T, R> {
  private ExecutorCompletionService<T> executor = new ExecutorCompletionService<>(newCachedThreadPool())

  //Its a little annoying to see code to catch -> unwrap -> rethrow in subclasses
  //When the original idea here was to allow them not to care about the OverQuotaException case
  //We can neatly avoid this since Groovy allows us to dynamically change a classes behaviour!!
  //Making this static so we don't waste time by doing this repeatedly, though it could potentially be done in a constructor
  //Or Application class?
  static {
    def savedMethod = Future.metaClass.getMetaMethod("get", [] as Class[])
    Future.metaClass.get = {
      ->
      try {
        return savedMethod.invoke(delegate)
      } catch (ExecutionException e) {
        throw e.getCause()
      }
    }
  }

  @Override
  protected Future<T> doCall(R request) {
    executor.submit({ super.doCall(request) })
  }
}
