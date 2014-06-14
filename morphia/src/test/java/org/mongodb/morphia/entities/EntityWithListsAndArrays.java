package org.mongodb.morphia.entities;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EntityWithListsAndArrays {
    private String[] array;
    private List<String> list;
    private ArrayList<String> arrayList;
    private String notAnArrayOrList;
}
