package com.opensymphony.xwork2.conversion;

/**
 * Instantiate converter classes, if cannot create TypeConverter throws exception
 */
public interface TypeConverterCreator {

    /**
     * Creates {@link TypeConverter} from given class
     *
     * @param className convert class
     * @return instance of {@link TypeConverter}
     * @throws Exception when cannot create/cast to {@link TypeConverter}
     */
    TypeConverter createTypeConverter(String className) throws Exception;

}
