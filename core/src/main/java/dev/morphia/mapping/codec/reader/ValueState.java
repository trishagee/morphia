package dev.morphia.mapping.codec.reader;

import org.bson.BsonType;

/**
 * @morphia.internal
 */
public class ValueState extends ReaderState {
    public static final String NAME = "VALUE";
    private final Object value;

    ValueState(DocumentReader reader, Object value) {
        super(reader);
        this.value = value;
    }

    BsonType getCurrentBsonType() {
        return reader().getBsonType(value);
    }

    @Override
    String getStateName() {
        return NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    <T> T value() {
        advance();
        return (T) value;
    }

    @Override
    void skipValue() {
        advance();
    }
}
