package framework.annotations.test;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Qualifier;
import framework.annotations.Service;

@Service
@Qualifier("test1")
public class Test1 {

    public String s1(){
        return "abra";
    }
}
