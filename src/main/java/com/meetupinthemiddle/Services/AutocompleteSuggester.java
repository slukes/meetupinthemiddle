package com.meetupinthemiddle.Services;

import java.util.Set;

public interface AutocompleteSuggester {
  public Set<String> makeSuggestions(String term);
}
