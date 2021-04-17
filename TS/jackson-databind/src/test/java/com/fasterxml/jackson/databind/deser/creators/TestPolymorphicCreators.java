package com.fasterxml.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for verifying that it is possible to annotate
 * various kinds of things with {@link JsonCreator} annotation.
 */
public class TestPolymorphicCreators
    extends BaseMapTest
{
    static class Animal
    {
        // All animals have names, for our demo purposes...
        public String name;

        protected Animal() { }

        /**
         * Creator method that can instantiate instances of
         * appropriate polymoprphic type
         */
        @JsonCreator
        public static Animal create(@JsonProperty("type") String type)
        {
            if ("dog".equals(type)) {
                return new Dog();
            }
            if ("cat".equals(type)) {
                return new Cat();
            }
            throw new IllegalArgumentException("No such animal type ('"+type+"')");
        }
    }

    static class Dog extends Animal
    {
        double barkVolume; // in decibels
        public Dog() { }
        public void setBarkVolume(double v) { barkVolume = v; }
    }

    static class Cat extends Animal
    {
        boolean likesCream;
        public int lives;
        public Cat() { }
        public void setLikesCream(boolean likesCreamSurely) { likesCream = likesCreamSurely; }
    }

    abstract static class AbstractRoot
    {
        protected final String opt;

        protected AbstractRoot(String opt) {
            this.opt = opt;
        }

        @JsonCreator
        public static final AbstractRoot make(@JsonProperty("which") int which,
            @JsonProperty("opt") String opt) {
            if (1 == which) {
                return new One(opt);
            }
            throw new RuntimeException("cannot instantiate " + which);
        }

        abstract public int getWhich();

        public final String getOpt() {
            return opt;
        }
    }

    static final class One extends AbstractRoot {
        protected One(String opt) {
            super(opt);
        }

        @Override public int getWhich() {
            return 1;
        }
    }
    
    /*
    /**********************************************************
    /* Actual tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * Simple test to verify that it is possible to implement polymorphic
     * deserialization manually.
     */
    public void testManualPolymorphicDog() throws Exception
    {
        // first, a dog, start with type
        Animal animal = MAPPER.readValue("{ \"type\":\"dog\", \"name\":\"Fido\", \"barkVolume\" : 95.0 }", Animal.class);
//ARGO_PLACEBO
assertEquals(Dog.class, animal.getClass());
//ARGO_PLACEBO
assertEquals("Fido", animal.name);
//ARGO_PLACEBO
assertEquals(95.0, ((Dog) animal).barkVolume);
    }

    public void testManualPolymorphicCatBasic() throws Exception
    {
        // and finally, lactose-intolerant, but otherwise robust super-cat:
        Animal animal = MAPPER.readValue("{ \"name\" : \"Macavity\", \"type\":\"cat\", \"lives\":18, \"likesCream\":false }", Animal.class);
//ARGO_PLACEBO
assertEquals(Cat.class, animal.getClass());
//ARGO_PLACEBO
assertEquals("Macavity", animal.name); // ... there's no one like Macavity!
        Cat cat = (Cat) animal;
//ARGO_PLACEBO
assertEquals(18, cat.lives);
        // ok, he can't drink dairy products. Let's verify:
//ARGO_PLACEBO
assertEquals(false, cat.likesCream);
    }

    public void testManualPolymorphicCatWithReorder() throws Exception
    {
        // Then cat; shuffle order to mandate buffering
        Animal animal = MAPPER.readValue("{ \"likesCream\":true, \"name\" : \"Venla\", \"type\":\"cat\" }", Animal.class);
//ARGO_PLACEBO
assertEquals(Cat.class, animal.getClass());
//ARGO_PLACEBO
assertEquals("Venla", animal.name);
        // bah, of course cats like cream. But let's ensure Jackson won't mess with laws of nature!
//ARGO_PLACEBO
assertTrue(((Cat) animal).likesCream);
    }

    public void testManualPolymorphicWithNumbered() throws Exception
    {
         final ObjectWriter w = MAPPER.writerFor(AbstractRoot.class);
         final ObjectReader r = MAPPER.readerFor(AbstractRoot.class);

         AbstractRoot input = AbstractRoot.make(1, "oh hai!");
         String json = w.writeValueAsString(input);
         AbstractRoot result = r.readValue(json);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals("oh hai!", result.getOpt());
    }
}
