package petespike.model;

public class PetesPikeException extends Exception {
    public PetesPikeException(String exception) throws Exception {
        throw new Exception(exception);
    }
}
