package ouday.challenge.com.service_locator;

import java.util.HashMap;

public class ServiceLocator {

    private HashMap<String, Object> instanceMap = new HashMap<>();
    private HashMap<Class<?>, Class<?>> classImplementationMap = new HashMap<>();

    public <T extends Destroyable, S extends T> ServiceLocator map(Class<T> classInterface, Class<S> classImplementation ){
        if (!classImplementationMap.containsKey(classInterface))
        classImplementationMap.put(classInterface, classImplementation);
        return this;
    }

    public <Service> Service getService(Class<Service> clazz)  {
        try {
            // 1- Check if there is already an instance created
            if (instanceMap.containsKey(clazz.getName()))
                return (Service) instanceMap.get(clazz.getName());

            // 2- Check if there is a mapping for the requested Service (interface)
            if (!classImplementationMap.containsKey(clazz))
                throw new NoMappingFoundException(clazz.getName()); // RuntimeException

            // 3- Create instance for the implementation class
            Class<?> impClass = classImplementationMap.get(clazz);

            Object instance = impClass.newInstance();
            instanceMap.put(clazz.getName(), instance);
            return (Service) instance;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public <Service extends Destroyable> void destroyService(Class<Service> clazz)  {
        try {
            if (!instanceMap.containsKey(clazz.getName()))
                return;
            if (!classImplementationMap.containsKey(clazz))
                return;
            ((Service)instanceMap.get(clazz)).destroyService();
            instanceMap.remove(clazz.getName());
            classImplementationMap.remove(clazz.getName());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
