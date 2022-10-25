package dev.morphia.aggregation.experimental.codecs.stages;

import dev.morphia.aggregation.experimental.stages.CollectionStats;
import dev.morphia.mapping.Mapper;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

import static dev.morphia.aggregation.experimental.codecs.ExpressionHelper.document;

public class CollectionStatsCodec extends StageCodec<CollectionStats> {
    public CollectionStatsCodec(Mapper mapper) {
        super(mapper);
    }

    @Override
    public Class<CollectionStats> getEncoderClass() {
        return CollectionStats.class;
    }

    @Override
    protected void encodeStage(BsonWriter writer, CollectionStats value, EncoderContext encoderContext) {
        document(writer, () -> {
            if (value.getHistogram()) {
                document(writer, "latencyStats", () -> writer.writeBoolean("histograms", true));
            }
            if (value.getScale() != null) {
                document(writer, "storageStats", () -> writer.writeInt32("scale", value.getScale()));
            }
            if (value.getCount()) {
                document(writer, "count", () -> {
                });
            }
        });
    }
}
