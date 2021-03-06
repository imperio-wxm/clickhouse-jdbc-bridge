package ru.yandex.clickhouse.jdbcbridge.db.clickhouse;

import ru.yandex.clickhouse.util.ClickHouseRowBinaryStream;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by krash on 26.09.18.
 */
public class ClickHouseFieldSerializer<T> {

    private final boolean canBeNull;
//    private final FieldValueExtractor<T> extractor;
//    private final FieldValueSerializer<T> serializer;
    private final ExtractorConverter<T> pair;

    public ClickHouseFieldSerializer(boolean fieldIsNullable, ExtractorConverter<T> pair) {
        canBeNull = fieldIsNullable;
        this.pair = pair;
    }

    public void serialize(ResultSet resultSet, int position, ClickHouseRowBinaryStream stream) throws SQLException, IOException {
        T value = pair.getExtractor().apply(resultSet, position);

        if (canBeNull) {
            final boolean isNull = resultSet.wasNull() || null == value;
            stream.writeByte((byte) (isNull ? 1 : 0));
            if (isNull) {
                return;
            }
        }
        pair.getSerializer().accept(value, stream);
    }
}
