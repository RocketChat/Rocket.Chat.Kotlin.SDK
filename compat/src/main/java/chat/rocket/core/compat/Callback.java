package chat.rocket.core.compat;

import javax.annotation.Nonnull;

import chat.rocket.common.RocketChatException;

public interface Callback<T> {
    void onSuccess(@Nonnull T data);

    void onError(@Nonnull RocketChatException error);
}
