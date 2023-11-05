package framework.annotations.test;

import framework.annotations.Qualifier;
import framework.annotations.Service;

@Service
@Qualifier("interface")
public class IntfTestImpl implements IntfTest{
    @Override
    public String s3() {
        return "dabra";
    }
}
