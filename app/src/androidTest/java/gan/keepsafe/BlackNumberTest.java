package gan.keepsafe;


import android.content.Context;
import android.test.AndroidTestCase;

import java.util.Random;

import gan.keepsafe.db.BlackNumberDao;

public class BlackNumberTest extends AndroidTestCase {
    private Context context;

    @Override
    protected void setUp() throws Exception {
        this.context = getContext();
        super.setUp();
    }

    public void testAdd(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            Long number = 18900000000l +i;
            dao.add(number +"",String.valueOf(random.nextInt(3) + 1));
        }
    }

}
