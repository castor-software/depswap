package com.fasterxml.jackson.databind.jsontype;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExistingPropertyTest extends BaseMapTest
{
    /**
     * Polymorphic base class - existing property as simple property on subclasses
     */
    @JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type",
            visible=true)
    @JsonSubTypes({
        @Type(value = Apple.class, name = "apple") ,
        @Type(value = Orange.class, name = "orange") 
    })
    static abstract class Fruit {
        public String name;
        protected Fruit(String n)  { name = n; }
    }

    @JsonTypeName("apple")
    @JsonPropertyOrder({ "name", "seedCount", "type" })
    static class Apple extends Fruit
    {
        public int seedCount;
        public String type;

        private Apple() { super(null); }
        public Apple(String name, int b) {
            super(name);
            seedCount = b;
            type = "apple";
        }
    }

    @JsonTypeName("orange")
    @JsonPropertyOrder({ "name", "color", "type" })
    static class Orange extends Fruit
    {
        public String color;
        public String type;

        private Orange() { super(null); }
        public Orange(String name, String c) {
            super(name);
            color = c;
            type = "orange";
        }
    }

    static class FruitWrapper {
        public Fruit fruit;
        public FruitWrapper() {}
        public FruitWrapper(Fruit f) { fruit = f; }
    }

    /**
     * Polymorphic base class - existing property forced by abstract method
     */
	@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type")
	@JsonSubTypes({
		@Type(value = Dog.class, name = "doggie") ,
		@Type(value = Cat.class, name = "kitty") 
		})
	static abstract class Animal {
        public String name;
        
        protected Animal(String n)  { name = n; }
        
        public abstract String getType();
    }

    @JsonTypeName("doggie")
    static class Dog extends Animal
    {
        public int boneCount;
        
        private Dog() { super(null); }
        public Dog(String name, int b) {
            super(name);
            boneCount = b;
        }
        
 		@Override
		public String getType() {
        	return "doggie";
        }        
    }

    @JsonTypeName("kitty")
    static class Cat extends Animal
    {
        public String furColor;
        
        private Cat() { super(null); }
        public Cat(String name, String c) {
            super(name);
            furColor = c;
        }
        
		@Override
		public String getType() {
        	return "kitty";
        }        
    }

    static class AnimalWrapper {
        public Animal animal;
        public AnimalWrapper() {}
        public AnimalWrapper(Animal a) { animal = a; }
    }

    /**
     * Polymorphic base class - existing property NOT forced by abstract method on base class
     */
	@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type")
	@JsonSubTypes({
		@Type(value = Accord.class, name = "accord") ,
		@Type(value = Camry.class, name = "camry") 
		})
	static abstract class Car {
        public String name;        
        protected Car(String n)  { name = n; }
    }

    @JsonTypeName("accord")
    static class Accord extends Car
    {
        public int speakerCount;
        
        private Accord() { super(null); }
        public Accord(String name, int b) {
            super(name);
            speakerCount = b;
        }
        
		public String getType() {
        	return "accord";
        }        
    }

    @JsonTypeName("camry")
    static class Camry extends Car
    {
        public String exteriorColor;
        
        private Camry() { super(null); }
        public Camry(String name, String c) {
            super(name);
            exteriorColor = c;
        }
        
		public String getType() {
        	return "camry";
        }        
    }

    static class CarWrapper {
        public Car car;
        public CarWrapper() {}
        public CarWrapper(Car c) { car = c; }
    }

    // for [databind#1635]

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            // IMPORTANT! Must be defined as `visible`
            visible=true,
            property = "type",
            defaultImpl=Bean1635Default.class)
    @JsonSubTypes({ @JsonSubTypes.Type(Bean1635A.class) })
    static class Bean1635 {
        public ABC type;
    }

    @JsonTypeName("A")
    static class Bean1635A extends Bean1635 {
        public int value;
    }

    static class Bean1635Default extends Bean1635 { }

    /*
    /**********************************************************
    /* Mock data
    /**********************************************************
     */

    private static final Orange mandarin = new Orange("Mandarin Orange", "orange");
    private static final String mandarinJson = "{\"name\":\"Mandarin Orange\",\"color\":\"orange\",\"type\":\"orange\"}";	
    private static final Apple pinguo = new Apple("Apple-A-Day", 16);    
    private static final String pinguoJson = "{\"name\":\"Apple-A-Day\",\"seedCount\":16,\"type\":\"apple\"}";
    private static final FruitWrapper pinguoWrapper = new FruitWrapper(pinguo);
    private static final String pinguoWrapperJson = "{\"fruit\":" + pinguoJson + "}";
    private static final List<Fruit> fruitList = Arrays.asList(pinguo, mandarin);
    private static final String fruitListJson = "[" + pinguoJson + "," + mandarinJson + "]";

    private static final Cat beelzebub = new Cat("Beelzebub", "tabby");
    private static final String beelzebubJson = "{\"name\":\"Beelzebub\",\"furColor\":\"tabby\",\"type\":\"kitty\"}";	
    private static final Dog rover = new Dog("Rover", 42);
    private static final String roverJson = "{\"name\":\"Rover\",\"boneCount\":42,\"type\":\"doggie\"}";
    private static final AnimalWrapper beelzebubWrapper = new AnimalWrapper(beelzebub);
    private static final String beelzebubWrapperJson = "{\"animal\":" + beelzebubJson + "}";
    private static final List<Animal> animalList = Arrays.asList(beelzebub, rover);
    private static final String animalListJson = "[" + beelzebubJson + "," + roverJson + "]";

    private static final Camry camry = new Camry("Sweet Ride", "candy-apple-red");
    private static final String camryJson = "{\"name\":\"Sweet Ride\",\"exteriorColor\":\"candy-apple-red\",\"type\":\"camry\"}";	
    private static final Accord accord = new Accord("Road Rage", 6);
    private static final String accordJson = "{\"name\":\"Road Rage\",\"speakerCount\":6,\"type\":\"accord\"}";
    private static final CarWrapper camryWrapper = new CarWrapper(camry);
    private static final String camryWrapperJson = "{\"car\":" + camryJson + "}";
    private static final List<Car> carList = Arrays.asList(camry, accord);
    private static final String carListJson = "[" + camryJson + "," + accordJson + "]";

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Fruits - serialization tests for simple property on sub-classes
     */
    public void testExistingPropertySerializationFruits() throws Exception
    {
        Map<String,Object> result = writeAndMap(MAPPER, pinguo);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(pinguo.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(pinguo.seedCount, result.get("seedCount"));
//ARGO_PLACEBO
assertEquals(pinguo.type, result.get("type"));
        
        result = writeAndMap(MAPPER, mandarin);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(mandarin.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(mandarin.color, result.get("color"));
//ARGO_PLACEBO
assertEquals(mandarin.type, result.get("type"));
        
        String pinguoSerialized = MAPPER.writeValueAsString(pinguo);
//ARGO_PLACEBO
assertEquals(pinguoSerialized, pinguoJson);

        String mandarinSerialized = MAPPER.writeValueAsString(mandarin);
//ARGO_PLACEBO
assertEquals(mandarinSerialized, mandarinJson);

        String fruitWrapperSerialized = MAPPER.writeValueAsString(pinguoWrapper);
//ARGO_PLACEBO
assertEquals(fruitWrapperSerialized, pinguoWrapperJson);

        String fruitListSerialized = MAPPER.writeValueAsString(fruitList);
//ARGO_PLACEBO
assertEquals(fruitListSerialized, fruitListJson);
    }

    /**
     * Fruits - deserialization tests for simple property on sub-classes
     */
    public void testSimpleClassAsExistingPropertyDeserializationFruits() throws Exception
    {
        Fruit pinguoDeserialized = MAPPER.readValue(pinguoJson, Fruit.class);
//ARGO_PLACEBO
assertTrue(pinguoDeserialized instanceof Apple);
//ARGO_PLACEBO
assertSame(pinguoDeserialized.getClass(), Apple.class);
//ARGO_PLACEBO
assertEquals(pinguo.name, pinguoDeserialized.name);
//ARGO_PLACEBO
assertEquals(pinguo.seedCount, ((Apple) pinguoDeserialized).seedCount);
//ARGO_PLACEBO
assertEquals(pinguo.type, ((Apple) pinguoDeserialized).type);

        FruitWrapper pinguoWrapperDeserialized = MAPPER.readValue(pinguoWrapperJson, FruitWrapper.class);
        Fruit pinguoExtracted = pinguoWrapperDeserialized.fruit;
//ARGO_PLACEBO
assertTrue(pinguoExtracted instanceof Apple);
//ARGO_PLACEBO
assertSame(pinguoExtracted.getClass(), Apple.class);
//ARGO_PLACEBO
assertEquals(pinguo.name, pinguoExtracted.name);
//ARGO_PLACEBO
assertEquals(pinguo.seedCount, ((Apple) pinguoExtracted).seedCount);
//ARGO_PLACEBO
assertEquals(pinguo.type, ((Apple) pinguoExtracted).type);

        Fruit[] fruits = MAPPER.readValue(fruitListJson, Fruit[].class);
//ARGO_PLACEBO
assertEquals(2, fruits.length);
//ARGO_PLACEBO
assertEquals(Apple.class, fruits[0].getClass());
//ARGO_PLACEBO
assertEquals("apple", ((Apple) fruits[0]).type);
//ARGO_PLACEBO
assertEquals(Orange.class, fruits[1].getClass());
//ARGO_PLACEBO
assertEquals("orange", ((Orange) fruits[1]).type);
        
        List<Fruit> f2 = MAPPER.readValue(fruitListJson,
                new TypeReference<List<Fruit>>() { });
//ARGO_PLACEBO
assertNotNull(f2);
//ARGO_PLACEBO
assertTrue(f2.size() == 2);
//ARGO_PLACEBO
assertEquals(Apple.class, f2.get(0).getClass());
//ARGO_PLACEBO
assertEquals(Orange.class, f2.get(1).getClass());
    }

    /**
     * Animals - serialization tests for abstract method in base class
     */
    public void testExistingPropertySerializationAnimals() throws Exception
    {
        Map<String,Object> result = writeAndMap(MAPPER, beelzebub);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(beelzebub.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(beelzebub.furColor, result.get("furColor"));
//ARGO_PLACEBO
assertEquals(beelzebub.getType(), result.get("type"));

        result = writeAndMap(MAPPER, rover);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(rover.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(rover.boneCount, result.get("boneCount"));
//ARGO_PLACEBO
assertEquals(rover.getType(), result.get("type"));
        
        String beelzebubSerialized = MAPPER.writeValueAsString(beelzebub);
//ARGO_PLACEBO
assertEquals(beelzebubSerialized, beelzebubJson);
        
        String roverSerialized = MAPPER.writeValueAsString(rover);
//ARGO_PLACEBO
assertEquals(roverSerialized, roverJson);
        
        String animalWrapperSerialized = MAPPER.writeValueAsString(beelzebubWrapper);
//ARGO_PLACEBO
assertEquals(animalWrapperSerialized, beelzebubWrapperJson);

        String animalListSerialized = MAPPER.writeValueAsString(animalList);
//ARGO_PLACEBO
assertEquals(animalListSerialized, animalListJson);
    }

    /**
     * Animals - deserialization tests for abstract method in base class
     */
    public void testSimpleClassAsExistingPropertyDeserializationAnimals() throws Exception
    {
        Animal beelzebubDeserialized = MAPPER.readValue(beelzebubJson, Animal.class);
//ARGO_PLACEBO
assertTrue(beelzebubDeserialized instanceof Cat);
//ARGO_PLACEBO
assertSame(beelzebubDeserialized.getClass(), Cat.class);
//ARGO_PLACEBO
assertEquals(beelzebub.name, beelzebubDeserialized.name);
//ARGO_PLACEBO
assertEquals(beelzebub.furColor, ((Cat) beelzebubDeserialized).furColor);
//ARGO_PLACEBO
assertEquals(beelzebub.getType(), beelzebubDeserialized.getType());

        AnimalWrapper beelzebubWrapperDeserialized = MAPPER.readValue(beelzebubWrapperJson, AnimalWrapper.class);
        Animal beelzebubExtracted = beelzebubWrapperDeserialized.animal;
//ARGO_PLACEBO
assertTrue(beelzebubExtracted instanceof Cat);
//ARGO_PLACEBO
assertSame(beelzebubExtracted.getClass(), Cat.class);
//ARGO_PLACEBO
assertEquals(beelzebub.name, beelzebubExtracted.name);
//ARGO_PLACEBO
assertEquals(beelzebub.furColor, ((Cat) beelzebubExtracted).furColor);
//ARGO_PLACEBO
assertEquals(beelzebub.getType(), beelzebubExtracted.getType());
    	
        @SuppressWarnings("unchecked")
        List<Animal> animalListDeserialized = MAPPER.readValue(animalListJson, List.class);
//ARGO_PLACEBO
assertNotNull(animalListDeserialized);
//ARGO_PLACEBO
assertTrue(animalListDeserialized.size() == 2);
        Animal cat = MAPPER.convertValue(animalListDeserialized.get(0), Animal.class);
//ARGO_PLACEBO
assertTrue(cat instanceof Cat);
//ARGO_PLACEBO
assertSame(cat.getClass(), Cat.class);
        Animal dog = MAPPER.convertValue(animalListDeserialized.get(1), Animal.class);
//ARGO_PLACEBO
assertTrue(dog instanceof Dog);
//ARGO_PLACEBO
assertSame(dog.getClass(), Dog.class);
    }

    /**
     * Cars - serialization tests for no abstract method or type variable in base class
     */
    public void testExistingPropertySerializationCars() throws Exception
    {
        Map<String,Object> result = writeAndMap(MAPPER, camry);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(camry.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(camry.exteriorColor, result.get("exteriorColor"));
//ARGO_PLACEBO
assertEquals(camry.getType(), result.get("type"));

        result = writeAndMap(MAPPER, accord);
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals(accord.name, result.get("name"));
//ARGO_PLACEBO
assertEquals(accord.speakerCount, result.get("speakerCount"));
//ARGO_PLACEBO
assertEquals(accord.getType(), result.get("type"));

        String camrySerialized = MAPPER.writeValueAsString(camry);
//ARGO_PLACEBO
assertEquals(camrySerialized, camryJson);

        String accordSerialized = MAPPER.writeValueAsString(accord);
//ARGO_PLACEBO
assertEquals(accordSerialized, accordJson);
        
        String carWrapperSerialized = MAPPER.writeValueAsString(camryWrapper);
//ARGO_PLACEBO
assertEquals(carWrapperSerialized, camryWrapperJson);

        String carListSerialized = MAPPER.writeValueAsString(carList);
//ARGO_PLACEBO
assertEquals(carListSerialized, carListJson);
    }

    /**
     * Cars - deserialization tests for no abstract method or type variable in base class
     */
    public void testSimpleClassAsExistingPropertyDeserializationCars() throws Exception
    {
        Car camryDeserialized = MAPPER.readValue(camryJson, Camry.class);
//ARGO_PLACEBO
assertTrue(camryDeserialized instanceof Camry);
//ARGO_PLACEBO
assertSame(camryDeserialized.getClass(), Camry.class);
//ARGO_PLACEBO
assertEquals(camry.name, camryDeserialized.name);
//ARGO_PLACEBO
assertEquals(camry.exteriorColor, ((Camry) camryDeserialized).exteriorColor);
//ARGO_PLACEBO
assertEquals(camry.getType(), ((Camry) camryDeserialized).getType());

        CarWrapper camryWrapperDeserialized = MAPPER.readValue(camryWrapperJson, CarWrapper.class);
        Car camryExtracted = camryWrapperDeserialized.car;
//ARGO_PLACEBO
assertTrue(camryExtracted instanceof Camry);
//ARGO_PLACEBO
assertSame(camryExtracted.getClass(), Camry.class);
//ARGO_PLACEBO
assertEquals(camry.name, camryExtracted.name);
//ARGO_PLACEBO
assertEquals(camry.exteriorColor, ((Camry) camryExtracted).exteriorColor);
//ARGO_PLACEBO
assertEquals(camry.getType(), ((Camry) camryExtracted).getType());

        @SuppressWarnings("unchecked")
        List<Car> carListDeserialized = MAPPER.readValue(carListJson, List.class);
//ARGO_PLACEBO
assertNotNull(carListDeserialized);
//ARGO_PLACEBO
assertTrue(carListDeserialized.size() == 2);
        Car result = MAPPER.convertValue(carListDeserialized.get(0), Car.class);
//ARGO_PLACEBO
assertTrue(result instanceof Camry);
//ARGO_PLACEBO
assertSame(result.getClass(), Camry.class);

        result = MAPPER.convertValue(carListDeserialized.get(1), Car.class);
//ARGO_PLACEBO
assertTrue(result instanceof Accord);
//ARGO_PLACEBO
assertSame(result.getClass(), Accord.class);
    }

    // for [databind#1635]: simple usage
    public void testExistingEnumTypeId() throws Exception
    {
        Bean1635 result = MAPPER.readValue(aposToQuotes("{'value':3, 'type':'A'}"),
                Bean1635.class);
//ARGO_PLACEBO
assertEquals(Bean1635A.class, result.getClass());
        Bean1635A bean = (Bean1635A) result;
//ARGO_PLACEBO
assertEquals(3, bean.value);
//ARGO_PLACEBO
assertEquals(ABC.A, bean.type);
    }

    // for [databind#1635]: verify that `defaultImpl` does not block assignment of
    // type id
    public void testExistingEnumTypeIdViaDefault() throws Exception
    {
        Bean1635 result = MAPPER.readValue(aposToQuotes("{'type':'C'}"),
                Bean1635.class);
//ARGO_PLACEBO
assertEquals(Bean1635Default.class, result.getClass());
//ARGO_PLACEBO
assertEquals(ABC.C, result.type);
    }
}
