package org.mongodb.morphia.entities;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EntityWithListsAndArrays {
    private String[] arrayOfStrings;
    private int[] arrayOfInts;
    private List<String> listOfStrings;
    private List<Integer> listOfIntegers;
    private ArrayList<String> arrayListOfStrings;
    private ArrayList<Integer> arrayListOfIntegers;
    private String notAnArrayOrList;
}
