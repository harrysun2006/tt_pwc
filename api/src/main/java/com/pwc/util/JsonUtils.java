package com.pwc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pwc.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper = null;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();

            bean.setIndentOutput(false);
            bean.setSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            // bean.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            bean.afterPropertiesSet();

            objectMapper = bean.getObject();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
            // objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
            objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            // If the decision to output enum as label values is made, enable the below line.
            // objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

            bean.setObjectMapper(objectMapper);
        }
        return objectMapper;
    }

    public static JavaType getParametricType(Class<?> classType, Class<?>... parameterTypes) {
        ObjectMapper mapper = getObjectMapper();
        TypeFactory factory = mapper.getTypeFactory();
        return factory.constructParametricType(classType, parameterTypes);
    }

    public static <T> T readValue(String value, Class<T> clazz) throws ApplicationException {
        return readValue(value, clazz, false);
    }

    public static <T> T readValue(String value, Class<T> clazz, boolean quiet) throws ApplicationException {
        try {
            if (value == null) return null;
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(value, clazz);
        } catch (Exception ex) {
            if (quiet) {
                log.warn("Failed to convert value to " + clazz, ex);
                return null;
            }
            throw new ApplicationException(ex);
        }
    }

    public static <T> T readValue(String value, TypeReference<T> ref) throws ApplicationException {
        return readValue(value, ref, false);
    }

    public static <T> T readValue(String value, TypeReference<T> ref, boolean quiet) throws ApplicationException {
        try {
            if (value == null) return null;
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(value, ref);
        } catch (Exception ex) {
            if (quiet) {
                log.warn("Failed to convert value to " + ref, ex);
                return null;
            }
            throw new ApplicationException(ex);
        }
    }

    public static <T> T readValue(String value, JavaType type) throws ApplicationException {
        return readValue(value, type, false);
    }

    public static <T> T readValue(String value, JavaType type, boolean quiet) throws ApplicationException {
        try {
            if (value == null) return null;
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(value, type);
        } catch (Exception ex) {
            if (quiet) {
                log.warn("Failed to convert value to " + type, ex);
                return null;
            }
            throw new ApplicationException(ex);
        }
    }

    public static String writeValue(Object object) throws ApplicationException {
        return writeValue(object, false);
    }

    public static String writeValue(Object object, boolean quiet) throws ApplicationException {
        try {
            if (object == null) return null;
            ObjectMapper mapper = getObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (Exception ex) {
            if (quiet) {
                log.warn(ex.getMessage(), ex);
                return null;
            }
            throw new ApplicationException(ex);
        }
    }

    public static JsonNode json2Node(String value) {
        return json2Node(value, false);
    }

    public static JsonNode json2Node(String value, boolean quiet) {
        try {
            ObjectMapper mapper = getObjectMapper();
            return value == null ? null : mapper.readTree(value);
        } catch (Exception ex) {
            if (quiet) {
                log.warn(ex.getMessage(), ex);
                return null;
            }
            throw new ApplicationException("Invalid json payload", ex);
        }
    }

    public static String node2Json(JsonNode node) {
        return node2Json(node, false);
    }

    public static String node2Json(JsonNode node, boolean quiet) {
        return writeValue(node, quiet);
    }

}
