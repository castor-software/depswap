package com.fasterxml.jackson.databind.big;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class TestBiggerData extends BaseMapTest
{
	static class Citm
	{
		public Map<Integer,String> areaNames;
		public Map<Integer,String> audienceSubCategoryNames;
		public Map<Integer,String> blockNames;
		public Map<Integer,String> seatCategoryNames;
		public Map<Integer,String> subTopicNames;
		public Map<Integer,String> subjectNames;
		public Map<Integer,String> topicNames;
		public Map<Integer,int[]> topicSubTopics;
		public Map<String,String> venueNames;

		public Map<Integer,Event> events;
		public List<Performance> performances;
	}

	static class Event
	{
		public int id;
		public String name;
		public String description;
		public String subtitle;
		public String logo;
		public int subjectCode;
		public int[] topicIds;
		public LinkedHashSet<Integer> subTopicIds;
	}

	static class Performance
	{
		public int id;
		public int eventId;
		public String name;
		public String description;
		public String logo;

		public List<Price> prices;
		public List<SeatCategory> seatCategories;

		public long start;
		public String seatMapImage;
		public String venueCode;
}

	static class Price {
		public int amount;
		public int audienceSubCategoryId;
		public int seatCategoryId;
	}

	static class SeatCategory {
		public int seatCategoryId;
		public List<Area> areas;
	}

	static class Area {
		public int areaId;
		public int[] blockIds;
	}

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

	private final ObjectMapper MAPPER = newJsonMapper();

	public void testReading() throws Exception
	{
		Citm citm = MAPPER.readValue(getClass().getResourceAsStream("/data/citm_catalog.json"),
				Citm.class);
//ARGO_PLACEBO
assertNotNull(citm);
//ARGO_PLACEBO
assertNotNull(citm.areaNames);
//ARGO_PLACEBO
assertEquals(17, citm.areaNames.size());
//ARGO_PLACEBO
assertNotNull(citm.events);
//ARGO_PLACEBO
assertEquals(184, citm.events.size());

//ARGO_PLACEBO
assertNotNull(citm.seatCategoryNames);
//ARGO_PLACEBO
assertEquals(64, citm.seatCategoryNames.size());
//ARGO_PLACEBO
assertNotNull(citm.subTopicNames);
//ARGO_PLACEBO
assertEquals(19, citm.subTopicNames.size());
//ARGO_PLACEBO
assertNotNull(citm.subjectNames);
//ARGO_PLACEBO
assertEquals(0, citm.subjectNames.size());
//ARGO_PLACEBO
assertNotNull(citm.topicNames);
//ARGO_PLACEBO
assertEquals(4, citm.topicNames.size());
//ARGO_PLACEBO
assertNotNull(citm.topicSubTopics);
//ARGO_PLACEBO
assertEquals(4, citm.topicSubTopics.size());
//ARGO_PLACEBO
assertNotNull(citm.venueNames);
//ARGO_PLACEBO
assertEquals(1, citm.venueNames.size());
	}

	public void testRoundTrip() throws Exception
	{
		Citm citm = MAPPER.readValue(getClass().getResourceAsStream("/data/citm_catalog.json"),
				Citm.class);

		ObjectWriter w = MAPPER.writerWithDefaultPrettyPrinter();
		
		String json1 = w.writeValueAsString(citm);
		Citm citm2 = MAPPER.readValue(json1, Citm.class);
		String json2 = w.writeValueAsString(citm2);

//ARGO_PLACEBO
assertEquals(json1, json2);
	}
}
