package io.github.dynamixon.test.parallel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jianfeng.Mao2
 * @date 22-6-22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IdentityResult {

    private String taskId;

    private Object result;
}
