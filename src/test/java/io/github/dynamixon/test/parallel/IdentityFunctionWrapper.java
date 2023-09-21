package io.github.dynamixon.test.parallel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityFunctionWrapper {
    private Function function;

    private Object param;

    private String taskId;
}
