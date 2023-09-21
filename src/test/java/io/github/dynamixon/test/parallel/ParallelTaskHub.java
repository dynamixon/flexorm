package io.github.dynamixon.test.parallel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class ParallelTaskHub {

    public static final ExecutorService PTASK_ES = new ThreadPoolExecutor(500,
        500,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(102400),
        new ThreadFactoryBuilder().setNameFormat("ptask-Thread-%d").setDaemon(true).build());

    public static <PARAM, RT> List<RT> exeTasksWithParam(List<FunctionWrapper<PARAM, RT>> tasks, ExecutorService es) {
        try {
            Map<String, Object> gtlMap = GeneralThreadLocal.get();
            List<CompletableFuture<RT>> futures = new ArrayList<>();

            for (FunctionWrapper<PARAM, RT> task : tasks) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        //propagate thread local
                        if(MapUtils.isNotEmpty(gtlMap)){
                            GeneralThreadLocal.set(new HashMap<>(gtlMap));
                        }
                        return task.getFunction().apply(task.getParam());
                    }catch (Exception e){
                        log.warn("exe task fail:Function[{}],Param[{}]",task.getFunction(),task.getParam(),e);
                        return null;
                    }finally {
                        GeneralThreadLocal.unset();
                    }
                }, es));
            }
            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            combinedFuture.get();
            return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, R> List<R> exeTasksWithParam(List<Function<T, R>> functions, T param, ExecutorService es) {
        List<FunctionWrapper<T,R>> tasks = new ArrayList<>();
        if(CollectionUtils.isEmpty(functions)){
            return new ArrayList<>();
        }
        functions.forEach(f -> tasks.add(new FunctionWrapper<>(f,param)));
        return exeTasksWithParam(tasks,es);
    }

    public static <T, R> List<R> exeTasksWithParam(List<Function<T, R>> functions, T param) {
        return exeTasksWithParam(functions,param,PTASK_ES);
    }

    public static Map<String,Object> exeTasksWithParamV2(List<IdentityFunctionWrapper> tasks, ExecutorService es) {
        try {
            Map<String, Object> gtlMap = GeneralThreadLocal.get();
            List<CompletableFuture<IdentityResult>> futures = new ArrayList<>();
            for (IdentityFunctionWrapper task : tasks) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        //propagate thread local
                        if(MapUtils.isNotEmpty(gtlMap)){
                            GeneralThreadLocal.set(new HashMap<>(gtlMap));
                        }
                        return new IdentityResult(task.getTaskId(),task.getFunction().apply(task.getParam()));
                    }catch (Exception e){
                        log.warn("exe task fail:Function[{}],Param[{}]",task.getFunction(),task.getParam(),e);
                        return null;
                    }finally {
                        GeneralThreadLocal.unset();
                    }
                }, es));
            }
            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            combinedFuture.get();
            List<IdentityResult> rtList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            Map<String,Object> mapRt = new ConcurrentHashMap<>();
            rtList.forEach(o-> mapRt.put(o.getTaskId(),o.getResult()));
            return mapRt;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public static <PARAM,RT> List<RT> exeTasksWithParam(List<FunctionWrapper<PARAM,RT>> tasks) {
        return exeTasksWithParam(tasks,PTASK_ES);
    }

    public static Map<String,Object> exeTasksWithParamV2(List<IdentityFunctionWrapper> tasks) {
        return exeTasksWithParamV2(tasks,PTASK_ES);
    }
}
