package cafe.mercury.anticheat.violation;

import lombok.Getter;

@Getter
public class Violation {

    private final Object[] violationData;

    public Violation(Object... violationData) {
        this.violationData = violationData;
    }

}
