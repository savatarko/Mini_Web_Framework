package framework.annotations.test;

import framework.annotations.*;

@Controller
public class TestSub {

    @Autowired(verbose = true)
    @Qualifier("test1")
    public Test1 test1;

    @Autowired(verbose = true)
    @Qualifier("test")
    public Test test;

    @Autowired(verbose = true)
    @Qualifier("interface")
    public IntfTest intfTest;

    @Path(path = "/test")
    @GET
    public String abrakadabra(){
        return test1.s1() + test.s2() + intfTest.s3();
    }
}
