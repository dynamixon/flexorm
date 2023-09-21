package io.github.dynamixon.test.parallel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionWrapper<PARAM, RT> {
    private Function<PARAM, RT> function;

    private PARAM param;
}
