package cafe.mercury.anticheat.check.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckData {

    //The name of the detection, for example "AutoClicker"
    String name();

    //The check identifier, for example "A"
    String type();

    //A description of how the check works
    String description();

    boolean experimental() default false;
}
