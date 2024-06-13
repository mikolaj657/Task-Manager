public enum TaskStatus {
    DO_ZROBIENIA("Do zrobienia"),
    W_TRAKCIE("W trakcie"),
    ZAKONCZONE("Zakończone");

    private final String translation;

    TaskStatus(String translation) {
        this.translation = translation;
    }

    public String translate() {
        return translation;
    }
}