package br.com.rcrios.smartportfolio.model;

import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

public class PersonTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  public static final String NAME = "JUNIT";
  public static final String CNPJ = "111.111.111/0001-01";

  @Test
  public void shouldThrowExceptionWithNull() {
    exception.expect(SmartPortfolioRuntimeException.class);
    Person.validate(null);
  }

  @Test
  public void validatedPersonShouldHaveName() {
    Person p = new Person();

    exception.expect(SmartPortfolioRuntimeException.class);
    Person.validate(p);
  }

  @Test
  public void validatedPersonShouldHaveValidName() {
    Person p = new Person();
    p.setName(" ");

    exception.expect(SmartPortfolioRuntimeException.class);
    Person.validate(p);
  }

  @Test
  public void validatedPersonShouldHaveCNPJ() {
    Person p = new Person();
    p.setName(NAME);

    exception.expect(SmartPortfolioRuntimeException.class);
    Person.validate(p);
  }

  @Test
  public void validatedPersonShouldHaveValidCNPJ() {
    Person p = new Person();
    p.setName(NAME);
    p.setNationalTaxPayerId(" ");

    exception.expect(SmartPortfolioRuntimeException.class);
    Person.validate(p);
  }

  @Test
  public void validPersonInstantiation() {
    Person p = PersonTest.factory();
    try {
      Person.validate(p);
    } catch (SmartPortfolioRuntimeException e) {
      fail("Should not have throwed an exception: " + e.getMessage());
    }
  }

  public static Person factory() {
    long tag = System.nanoTime();

    Person person = new Person();
    person.setName("NAME_" + tag);
    person.setNationalTaxPayerId(String.valueOf(tag));
    person.setNickname("NICKNAME");
    person.setType(PersonType.LEGAL);

    return person;
  }
}
