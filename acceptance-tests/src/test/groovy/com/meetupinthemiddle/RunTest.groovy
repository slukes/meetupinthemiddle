package com.meetupinthemiddle
import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber)
@CucumberOptions(features = ['src/test/resources/features/'],
    format = 'pretty')
class RunTest {
}
