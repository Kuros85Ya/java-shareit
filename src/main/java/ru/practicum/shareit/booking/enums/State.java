package ru.practicum.shareit.booking.enums;

import org.springframework.core.convert.converter.Converter;

/** Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
 * Также он может принимать значения CURRENT (англ. «текущие»),
 * PAST (англ. «завершённые»),
 * FUTURE (англ. «будущие»),
 * WAITING (англ. «ожидающие подтверждения»),
 * REJECTED (англ. «отклонённые»)**/
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static class StringToEnumConverter implements Converter<String, State> {
        @Override
        public State convert(String source) {
            return State.valueOf(source.toUpperCase());
        }
    }
}
