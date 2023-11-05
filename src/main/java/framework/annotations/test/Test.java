package framework.annotations.test;

import framework.annotations.*;

@Bean(scope = BeanType.PROTOTYPE)
@Qualifier("test")
public class Test {
    @Autowired(verbose = true)
    @Qualifier("test1")
    public Test1 test1;

    public String s2(){
        return "ka";
    }
}
