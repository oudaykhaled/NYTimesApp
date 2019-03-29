package ouday.challenge.com.service_locator;

class NoMappingFoundException extends RuntimeException {
    public NoMappingFoundException(String name) {
        super(name);
    }
}
