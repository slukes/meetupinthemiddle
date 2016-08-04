package com.meetupinthemiddle.model

import org.junit.Test

import static com.meetupinthemiddle.model.TransportMode.DRIVING

/**
 * Test class for Person.
 * This is mainly to test out the @Builder and @EqualsAndHashCode annotations
 */
class PersonTest {
  @Test
  void testEquals() {
    //Given
    def person = aPerson()
    def identicalPerson = aPerson()

    //When
    def result = person.equals(identicalPerson)

    //Then
    assert result
  }

  @Test
  void testNotEquals() {
    //Given
    def person = aPerson()
    def differentPerson = aDifferentPerson()

    //When
    //== in groovy calls .equals()
    def result = person == differentPerson

    //Then
    assert !result
  }

  @Test
  void testHashCode() {
    //Given
    def person = aPerson()
    def identicalPerson = aPerson()

    //When
    def hash1 = person.hashCode()
    def hash2 = identicalPerson.hashCode()

    //Then
    assert hash1 == hash2
  }

  @Test
  void testDifferentHashCode() {
    //Given
    def person = aPerson()
    Person differentPerson = aDifferentPerson()

    //When
    def hash1 = person.hashCode()
    def hash2 = differentPerson.hashCode()

    //Then
    assert hash1 != hash2
  }

  @Test
  void testBuilder(){
    //Given
    def person = aPerson()

    //Then
    person.with {
      assert from == "Woking"
      latLong.lat == 2
      latLong.lng == 3
      distance == 5
      name == "George"
      transportMode == DRIVING
    }
  }

  private aDifferentPerson() {
    def differentPerson = aPerson()
    differentPerson.from = "Guildford"
    differentPerson
  }

  private aPerson() {
    Person.builder()
        .withFrom("Woking")
        .withLatLong(new LatLong(2, 3))
        .withDistance(5)
        .withName("George")
        .withTransportMode(DRIVING)
        .build()
  }
}