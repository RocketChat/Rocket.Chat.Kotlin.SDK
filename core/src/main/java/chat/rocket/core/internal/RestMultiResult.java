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

public class RestMultiResult<T> {
    private T update;
    private T remove;

    private RestMultiResult(T update, T remove) {
        this.update = update;
        this.remove = remove;
    }

    public T getUpdate(){
        return update;
    }

    public T getRemove() {
        return remove;
    }

    @Override
    public String toString() {
        return "RestMultiResult{" +
                "update=" + update +
                ", remove=" + remove +
                '}';
    }

    public static <T> RestMultiResult<T> create(T update, T remove) {
        return new RestMultiResult<>(update, remove);
    }

    public static class MoshiJsonAdapter<T> extends JsonAdapter<RestMultiResult<T>> {
        private static final String[] NAMES = new String[] {"update", "remove"};
        private static final JsonReader.Options OPTIONS = JsonReader.Options.of(NAMES);
        private final JsonAdapter<T> tAdaptper;

        MoshiJsonAdapter(Moshi moshi, Type[] types) {
            this.tAdaptper = adapter(moshi, types[0]);
        }

        @Nullable
        @Override
        public RestMultiResult<T> fromJson(@NotNull JsonReader reader) throws IOException {
            reader.beginObject();
            T update = null;
            T remove = null;
            while (reader.hasNext()) {
                switch (reader.selectName(OPTIONS)) {
                    case 0: {
                        update = this.tAdaptper.fromJson(reader);
                        break;
                    }
                    case 1: {
                        remove = this.tAdaptper.fromJson(reader);
                        break;
                    }
                    default: {
                        reader.nextName();
                        reader.skipValue();
                    }
                }
            }
            reader.endObject();
            return RestMultiResult.create(update, remove);
        }

        @Override
        public void toJson(@NotNull JsonWriter writer, @Nullable RestMultiResult<T> value)
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
                if (rawType.equals(RestMultiResult.class)) {
                    return new RestMultiResult.MoshiJsonAdapter(moshi,
                            ((ParameterizedType) type).getActualTypeArguments());
                }
            }
            return null;
        }
    }
}
