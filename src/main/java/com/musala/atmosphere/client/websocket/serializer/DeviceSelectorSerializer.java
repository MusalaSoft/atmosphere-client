package com.musala.atmosphere.client.websocket.serializer;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceParameter;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;

public class DeviceSelectorSerializer implements JsonSerializer<DeviceSelector> {

    @Override
    public JsonElement serialize(DeviceSelector selector, Type type, JsonSerializationContext context) {
        JsonArray rootElement = new JsonArray();

        Map<Class<? extends DeviceParameter>, DeviceParameter> parameters = selector.getParameters();
        for (Map.Entry<Class<? extends DeviceParameter>, DeviceParameter> entry : parameters.entrySet()) {
            Gson gson = new Gson();
            JsonArray parameterArray = new JsonArray();
            String className = entry.getKey().getName();
            String parameter = gson.toJson(entry.getValue(), entry.getKey());
            parameterArray.add(className);
            parameterArray.add(parameter);

            rootElement.add(parameterArray);
        }
        return rootElement;
    }

}
