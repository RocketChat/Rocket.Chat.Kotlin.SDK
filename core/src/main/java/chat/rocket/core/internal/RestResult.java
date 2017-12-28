package chat.rocket.core.internal;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

public class RestResult<T> {
    private T _result;
    @Nullable private Long total = null;
    @Nullable private Long offset = null;
    @Nullable private Long count = null;

    private RestResult(T data, @Nullable Long total, @Nullable Long offset, @Nullable Long count) {
        this._result = data;
        this.total = total;
        this.offset = offset;
        this.count = count;
    }

    public T result(){
        return _result;
    }

    @Nullable
    public Long total() {
        return total;
    }

    @Nullable
    public Long offset() {
        return offset;
    }
    @Nullable
    public Long count() {
        return count;
    }

    static <T> RestResult<T> create(T data, Long total, Long offset, Long count) {
        return new RestResult<>(data, total, offset, count);
    }

    public static class MoshiJsonAdapter<T> extends JsonAdapter<RestResult<T>> {
        private static final String[] NAMES = new String[] {"status", "success", "total",
                "offset", "count"};
        private static final JsonReader.Options OPTIONS = JsonReader.Options.of(NAMES);
        private final JsonAdapter<T> tAdaptper;

        MoshiJsonAdapter(Moshi moshi, Type[] types) {
            this.tAdaptper = adapter(moshi, types[0]);
        }

        @Nullable
        @Override
        public RestResult<T> fromJson(@NotNull JsonReader reader) throws IOException {
            reader.beginObject();
            T result = null;
            Long total = null;
            Long offset = null;
            Long count = null;
            while (reader.hasNext()) {
                switch (reader.selectName(OPTIONS)) {
                    case 0:
                    case 1: {
                        // Just ignore status or success value, we are parsing 200 OK messages
                        reader.skipValue();
                        break;
                    }
                    case 2: {
                        total = reader.nextLong();
                        break;
                    }
                    case 3: {
                        offset = reader.nextLong();
                        break;
                    }
                    case 4: {
                        count = reader.nextLong();
                        break;
                    }
                    case -1: {
                        reader.nextName();
                        JsonReader.Token token = reader.peek();
                        if (token == JsonReader.Token.BEGIN_ARRAY
                                || token == JsonReader.Token.BEGIN_OBJECT) {
                            result = this.tAdaptper.fromJson(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                }
            }
            reader.endObject();
            return RestResult.create(result, total, offset, count);
        }

        @Override
        public void toJson(@NotNull JsonWriter writer, @Nullable RestResult<T> value)
                throws IOException {

        }

        private JsonAdapter<T> adapter(Moshi moshi, Type adapterType) {
            return moshi.adapter(adapterType);
        }
    }

    public static class JsonAdapterFactory implements JsonAdapter.Factory {
        @Nullable
        @Override
        public JsonAdapter<?> create(@NotNull Type type,
                                     @NotNull Set<? extends Annotation> annotations,
                                     @NotNull Moshi moshi) {
            if (!annotations.isEmpty()) return null;
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType.equals(RestResult.class)) {
                    return new RestResult.MoshiJsonAdapter(moshi,
                            ((ParameterizedType) type).getActualTypeArguments());
                }
            }
            return null;
        }
    }
}
