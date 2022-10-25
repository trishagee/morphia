package dev.morphia.mapping.codec.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bson.Document;

class ArrayState extends ValueState<List<Object>> {
    private final List<Object> list = new ArrayList<>();
    private boolean finished = false;
    private ValueState<?> substate;

    ArrayState(DocumentWriter writer, WriteState previous) {
        super(writer, previous);
    }

    public List<Object> getList() {
        return list;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", finished ? "]" : "");
        list.forEach(v -> joiner.add(v instanceof Document ? ((Document) v).toJson() : String.valueOf(v)));
        if (substate != null) {
            joiner.add(substate.toString());
        }
        return joiner.toString();
    }

    @Override
    public List<Object> value() {
        return list;
    }

    @Override
    protected String state() {
        return "array";
    }

    @Override
    void value(Object value) {
        list.add(value);
    }

    @Override
    WriteState array() {
        substate = new ArrayState(getWriter(), this);
        return substate;
    }

    @Override
    WriteState document() {
        substate = new DocumentState(getWriter(), this);
        return substate;
    }

    @Override
    void done() {
        if (substate != null) {
            list.add(substate.value());
            substate = null;
        }
        super.done();
    }

    @Override
    void end() {
        finished = true;
        if (substate != null) {
            substate.end();
        }
        super.end();
    }
}
