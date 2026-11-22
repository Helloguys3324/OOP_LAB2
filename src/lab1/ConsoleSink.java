package lab1;

public class ConsoleSink {
    // The ONLY place in the entire logging system that prints to the console.
    public void write(String line) {
        System.out.println(line);
    }
}
