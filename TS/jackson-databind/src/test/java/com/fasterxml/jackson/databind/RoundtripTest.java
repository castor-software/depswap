package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.testutil.MediaItem;

public class RoundtripTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();
    
    public void testMedaItemRoundtrip() throws Exception
    {
        MediaItem.Content c = new MediaItem.Content();
        c.setBitrate(9600);
        c.setCopyright("none");
        c.setDuration(360000L);
        c.setFormat("lzf");
        c.setHeight(640);
        c.setSize(128000L);
        c.setTitle("Amazing Stuff For Something Or Oth\u00CBr!");
        c.setUri("http://multi.fario.us/index.html");
        c.setWidth(1400);

        c.addPerson("Joe Sixp\u00e2ck");
        c.addPerson("Ezekiel");
        c.addPerson("Sponge-Bob Squarepant\u00DF");
        
        MediaItem input = new MediaItem(c);
        input.addPhoto(new MediaItem.Photo());
        input.addPhoto(new MediaItem.Photo());
        input.addPhoto(new MediaItem.Photo());

        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(input);

        MediaItem output = MAPPER.readValue(new java.io.StringReader(json), MediaItem.class);
//ARGO_PLACEBO
assertNotNull(output);

//ARGO_PLACEBO
assertNotNull(output.getImages());
//ARGO_PLACEBO
assertEquals(input.getImages().size(), output.getImages().size());
//ARGO_PLACEBO
assertNotNull(output.getContent());
//ARGO_PLACEBO
assertEquals(input.getContent().getTitle(), output.getContent().getTitle());
//ARGO_PLACEBO
assertEquals(input.getContent().getUri(), output.getContent().getUri());

        // compare re-serialization as a simple check as well
//ARGO_PLACEBO
assertEquals(json, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(output));
    }
}
