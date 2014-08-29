package com.cs.distr.shared.api;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author: artsiom.kotov
 */
public class Runner {
    public static void main(String[] args) throws Exception{
        MongoClient mongoClient = new MongoClient("centos.db",27039);
        DB db = mongoClient.getDB("school");
        DBCollection collection = db.getCollection("students");
        DBCursor homeworkCursor = collection.find().sort(new BasicDBObject("dsfd",-1));
        while(homeworkCursor.hasNext()) {
            DBObject object = homeworkCursor.next();
            BasicDBList scores = (BasicDBList)object.get("scores");

            ListIterator<Object> iterator = scores.listIterator();
            Double minScore = Double.MAX_VALUE;
            DBObject removed = null;
            int entire = 0;
            while(iterator.hasNext()) {
                DBObject currentObject = (DBObject)iterator.next();
                if ("homework".equals(currentObject.get("type"))) {
                    entire++;
                    Double currentScore = (Double)currentObject.get("score");
                    if (currentScore != null && minScore > currentScore) {
                        minScore = currentScore;
                        removed = currentObject;
                    }
                }
            }
            if (entire > 1 && removed != null) {
                scores.remove(removed);
                collection.save(object);
            }
        }
    }
}
