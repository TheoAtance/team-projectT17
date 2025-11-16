package use_case.custom_register;

/**
 * Input Boundary interface for the Register Use Case.
 */
public interface RegisterInputBoundary {
    /**
     * Executes the registration process using the provided input data.
     * @param inputData The data transfer object containing email, password, etc.
     */
    void execute(RegisterInputData inputData);
}