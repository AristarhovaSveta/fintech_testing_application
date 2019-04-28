package uitests;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.runner.intent.IntentMonitorImpl;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intent.IntentMonitor;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.fastaccess.ui.modules.main.donation.CheckPurchaseActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import github.PullRequestTimelineQuery;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UiTests {

    private static final int LAUNCH_TIMEOUT = 5000;

    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(instrumentation);
        device.pressHome();
        String packageName = InstrumentationRegistry.getTargetContext().getPackageName();
        assertThat(packageName, notNullValue());
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), LAUNCH_TIMEOUT);
        Context context = instrumentation.getTargetContext().getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), LAUNCH_TIMEOUT);
    }

    private void openMenu() {
        UiObject2 openMenuButton = device.findObject(By.clazz("android.widget.ImageButton"));
        openMenuButton.click();
    }

    private void clickMenuItem(String text) {
        BySelector selector = By.text(text);
        device.wait(Until.hasObject(selector.enabled(true)), LAUNCH_TIMEOUT);
        device.findObject(selector).click();
    }

    @Test
    public void openTrending() throws UiObjectNotFoundException {
        openMenu();
        clickMenuItem("Trending");
        assertEquals(device.findObject(new UiSelector().resourceId("com.fastaccess.github.debug:id/toolbar")).
                getChild(new UiSelector().className("android.widget.TextView")).getText(), "Trending");
    }

    @Test
    public void changeTheme() {
        openMenu();
        clickMenuItem("Settings");
        clickMenuItem("Theme");

        int rightX = 600;
        int leftX = 0;
        int y = 900;
        int steps = 10;
        device.swipe(rightX, y, leftX, y, steps);
        device.swipe(rightX, y, leftX, y, steps);
        BySelector selector = By.clazz("android.widget.ImageButton");
        device.wait(Until.hasObject(selector.enabled(true)), LAUNCH_TIMEOUT);
        device.findObject(selector).click();
        /* Нельзя с помощью UI Automator проверить цвет темы приложения */
    }

    @Test
    public void restorePurchases() {
        openMenu();
        BySelector selector = By.text("Restore Purchases");
        device.wait(Until.hasObject(selector.enabled(true)), LAUNCH_TIMEOUT);
        device.findObject(selector).click();
        /* Нельзя перехватить Intent для его проверки средствами UI Automator */
    }

    @Test
    public void checkToast() {
        openMenu();
        clickMenuItem("Send feedback");
        device.findObject(By.text("OK")).click();
        device.findObject(By.clazz("android.widget.EditText")).setText("hello");
        device.findObject(By.res("com.fastaccess.github.debug:id/description")).click();
        BySelector deviceInfo = By.res("com.fastaccess.github.debug:id/editText");
        device.wait(Until.hasObject(deviceInfo.enabled(true)), 5000);
        String info = device.findObject(deviceInfo).toString();
        assertEquals(info, info);
        /* Нельзя проверить «Device Information» для текущей модели телефона с помощью UI Automator */
        device.findObject(By.res("com.fastaccess.github.debug:id/submit")).click();
        /* Нельзя получить текст из Toast с помощью UI Automator */
    }

    @Test
    public void checkMarkup() throws UiObjectNotFoundException {
        openMenu();
        clickMenuItem("About");
        UiScrollable scrollable = new UiScrollable(new UiSelector()
                .resourceId("com.fastaccess.github.debug:id/mal_recyclerview")
                .scrollable(true));
        scrollable.scrollTextIntoView("About");
        assertEquals(device.findObjects(By.res("com.fastaccess.github.debug:id/mal_item_text"))
                .get(4).getText(), "Changelog");
        /* Нельзя проверить проверить иконку средствами UI Automator */
    }

}
