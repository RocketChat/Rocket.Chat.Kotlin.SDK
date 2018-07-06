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

public class RestMultiResult<T1, T2> {
    private T1 update;
    private T2 remove;

    private RestMultiResult(T1 update, T2 remove) {
        this.update = update;
        this.remove = remove;
    }

    public T1 getUpdate(){
        return update;
    }

    public T2 getRemove() {
        return remove;
    }

    @Override
    public String toString() {
        return "RestMultiResult{" +
                "update=" + update +
                ", remove=" + remove +
                '}';
    }

    public static <T1, T2> RestMultiResult<T1, T2> create(T1 update, T2 remove) {
        return new RestMultiResult<>(update, remove);
    }

    public static class MoshiJsonAdapter<T1, T2> extends JsonAdapter<RestMultiResult<T1, T2>> {
        private static final String[] NAMES = new String[] {"update", "remove"};
        private static final JsonReader.Options OPTIONS = JsonReader.Options.of(NAMES);
        private final JsonAdapter<T1> t1Adaptper;
        private final JsonAdapter<T2> t2Adaptper;

        MoshiJsonAdapter(Moshi moshi, Type[] types) {
            this.t1Adaptper = adapter(moshi, types[0]);
            this.t2Adaptper = adapter2(moshi, types[1]);
        }

        @Nullable
        @Override
        public RestMultiResult<T1, T2> fromJson(@NotNull JsonReader reader) throws IOException {
            reader.beginObject();
            T1 update = null;
            T2 remove = null;
            while (reader.hasNext()) {
                switch (reader.selectName(OPTIONS)) {
                    case 0: {
                        update = this.t1Adaptper.fromJson(reader);
                        break;
                    }
                    case 1: {
                        remove = this.t2Adaptper.fromJson(reader);
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
        public void toJson(@NotNull JsonWriter writer, @Nullable RestMultiResult<T1, T2> value)
                throws IOException {

        }

        private JsonAdapter<T1> adapter(Moshi moshi, Type adapterType) {
            return moshi.adapter(adapterType);
        }

        private JsonAdapter<T2> adapter2(Moshi moshi, Type adapterType) {
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
