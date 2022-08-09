package com.sensorsdata.analytics.javasdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sensorsdata.analytics.javasdk.bean.EventRecord;
import com.sensorsdata.analytics.javasdk.bean.ItemRecord;
import com.sensorsdata.analytics.javasdk.bean.UserRecord;
import com.sensorsdata.analytics.javasdk.consumer.BatchConsumer;
import com.sensorsdata.analytics.javasdk.exceptions.InvalidArgumentException;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  适用于 v3.4.4+ 版本
 *  测试点：验证事件的 _track_id 正常由 $track_id 生成，time 由 $time 生成。
 *  无特殊情况，不在下面的 testcase 上一一说明
 */
public class IDMappingModel1TrackIdAndTime extends SensorsBaseTest {

  private BatchConsumer batchConsumer;

  private List<Map<String, Object>> messageList;
  SensorsAnalytics sa;

  @Before
  public void init() throws NoSuchFieldException, IllegalAccessException {

    String url = "http://10.120.111.143:8106/sa?project=default";
    // 注意要设置 bulkSize 稍微大一点，这里设置为 100，否则超过 1 条就上报，messageList 里面拿不到事件数据
    batchConsumer = new BatchConsumer(url, 100, true, 3);
    // 通过反射机制获取 BatchConsumer 的 messageList
    Field field = batchConsumer.getClass().getDeclaredField("messageList"); // messageList 是 BatchConsumer 用来暂存事件数据的成员变量
    field.setAccessible(true);
    messageList = (List<Map<String, Object>>) field.get(batchConsumer);
    sa = new SensorsAnalytics(batchConsumer);
  }

  private void assertNotNullProp(){
    assertNotNull(messageList.get(0).get("time"));
    assertNotNull(messageList.get(0).get("_track_id"));
    assertNotNull(messageList.get(0).get("properties"));
  }

  /**
   * 校验调用 track 方法生成事件节点数是否完整
   */
  @Test
  public void checkTrackEventLoginTrue() throws InvalidArgumentException {
    Map<String, Object> properties = new HashMap<>();
        properties.put("$track_id", 111);
    Date date = new Date();
    properties.put("$time", date);
    sa.track("123", true, "test", properties);
    assertNotNullProp();

    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id

  }


  /**
   * 校验 trackSignup 记录节点
   */
  @Test
  public void checkTrackSignUpProp() throws InvalidArgumentException {
    Map<String, Object> properties = new HashMap<>();
    properties.put("number1", 1234);
    properties.put("String1", "str");
    properties.put("boolean1", false);
        properties.put("$track_id", 111);
    Date date = new Date();
    properties.put("$time", date);
    sa.trackSignUp("123", "345",  properties);

    assertNotNullProp();

    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }


  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void checkProfileSetDataType() throws InvalidArgumentException {
    List<String> list = new ArrayList<>();
    Date date = new Date();
    list.add("aaa");
    list.add("bbb");
    Map<String, Object> properties = new HashMap<>();
    properties.put("number1", 1234);
    properties.put("date1", date);
    properties.put("String1", "str");
    properties.put("boolean1", false);
    properties.put("list1", list);
        properties.put("$track_id", 111);
    properties.put("$time", date);
    sa.profileSet("123", true, properties);

    assertNotNullProp();

    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
    assertTrue(props.containsKey("number1")); // properties 包含其他自定义属性

  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void checkProfileSetDataType01() throws InvalidArgumentException {

    sa.profileSet("123", true, "$track_id", 111);
    assertNotNullProp();

    assertEquals(111, messageList.get(0).get("_track_id"));
//    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id

  }
  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void testProfileSetOnceDataType() throws InvalidArgumentException {
    List<String> list = new ArrayList<>();
    Date date = new Date();
    list.add("aaa");
    list.add("bbb");
    Map<String, Object> properties = new HashMap<>();
    properties.put("number1", 1234);
    properties.put("date1", date);
    properties.put("String1", "str");
    properties.put("boolean1", false);
    properties.put("list1", list);
        properties.put("$track_id", 111);
    properties.put("$time", date);
    sa.profileSetOnce("123", true, properties);

    assertNotNullProp();

    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
    assertTrue(props.containsKey("number1")); // properties 包含其他自定义属性

  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void testProfileIncrement() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("number1", 1234);
        properties.put("$track_id", 111);
    Date date = new Date();
    properties.put("$time", date);
    try {
      sa.profileIncrement("123", true, properties);
        fail("[ERROR] profileIncrement should throw InvalidArgumentException.");
    }catch (Exception e){
      e.printStackTrace();
      assertEquals("The property value of PROFILE_INCREMENT should be a Number.The current type is class java.util.Date.", e.getMessage());
    }
  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void testProfileIncrement01(){
    Date date = new Date();
    try {
      sa.profileIncrement("123", true, "$time", date.getTime());
      fail("[ERROR] profileIncrement should throw InvalidArgumentException.");
    }catch (Exception e){
      e.printStackTrace();
      assertEquals("The property '$time' should be a java.util.Date.", e.getMessage());
    }
  }


  @Test
  public void testProfileAppend() {
    List<String> list = new ArrayList<>();
    list.add("aaa");
    list.add("bbb");
    Map<String, Object> properties = new HashMap<>();
    properties.put("list1", list);

    List<Integer> listInt = new ArrayList<>();
    listInt.add(111);
    properties.put("$track_id", listInt);

    try {
      sa.profileAppend("123", true, properties);
      fail("[ERROR] profileAppend should throw InvalidArgumentException.");
    }catch (Exception e){
      e.printStackTrace();
      assertEquals("The property '$track_id' should be a list of String.", e.getMessage());
    }

  }

  @Test
  public void testProfileAppend01() throws InvalidArgumentException{
    sa.profileAppend("123", true, "$track_id", "111");

    assertNotNullProp();

    assertNotEquals(111, messageList.get(0).get("_track_id"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id

  }

  // profileUnset
  @Test
  public void testProfileUnset() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("list1", true);
        properties.put("$track_id", 111);
    Date date = new Date();
    properties.put("$time", date);

    try {
      sa.profileUnset("123", true, properties);
      fail("[ERROR] profileUnset should throw InvalidArgumentException.");
    }catch (InvalidArgumentException e){
      assertEquals("The property value of $time should be true.", e.getMessage());
    }

  }

  // profileUnset
  @Test
  public void testProfileUnset01() throws InvalidArgumentException{
    sa.profileUnset("123", true, "$track_id");

    assertNotNullProp();

    assertNotEquals(111, messageList.get(0).get("_track_id"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id

  }

  // profileDelete
  @Test
  public void testProfileDelete() throws InvalidArgumentException{
    sa.profileDelete("123", true);
  }

  @Test
  public void testItemSet_Delete() throws Exception {
    Date date = new Date();
    //物品纬度表上报
    String itemId = "product001", itemType = "mobile";
    ItemRecord addRecord = ItemRecord.builder().setItemId(itemId).setItemType(itemType)
            .addProperty("color", "white")
            .addProperty("$time", date)
            .build();
    sa.itemSet(addRecord);
    System.out.println(date.getTime());

    //删除物品纬度信息
    ItemRecord deleteRecord = ItemRecord.builder().setItemId(itemId).setItemType(itemType)
            .addProperty("$time", date)
            .build();
    sa.itemDelete(deleteRecord);
    sa.flush();
  }



  /**
   * 校验 event Builder 模式生成数据用户属性是否正常
   */
  @Test
  public void checkTrackEventBuilder() throws InvalidArgumentException {
    Date date = new Date();
    EventRecord eventRecord = EventRecord.builder()
            .setDistinctId("abc")
            .isLoginId(false)
            .setEventName("test")
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .build();
    sa.track(eventRecord);

    assertNotNullProp();
    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }

  /**
   * 校验 is_login_id 为 true 的事件属性
   */
  @Test
  public void checkTrackEventBuilderLoginIdIsTrue() throws InvalidArgumentException {
    Date date = new Date();
    EventRecord eventRecord = EventRecord.builder()
            .setDistinctId("abc")
            .isLoginId(true)
            .setEventName("test")
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .build();
    sa.track(eventRecord);

    assertNotNullProp();
    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void checkProfileSetDataTypeEventBuilder() throws InvalidArgumentException {
    List<String> list = new ArrayList<>();
    Date date = new Date();
    list.add("aaa");
    list.add("bbb");
    UserRecord userRecord = UserRecord.builder()
            .setDistinctId("123")
            .isLoginId(true)
            .addProperty("number1", 1234)
            .addProperty("date1", date)
            .addProperty("String1", "str")
            .addProperty("boolean1", false)
            .addProperty("list1", list)
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .build();
    sa.profileSet(userRecord);

    assertNotNullProp();
    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void testProfileSetOnceDataTypeEventBuilder() throws InvalidArgumentException {
    List<String> list = new ArrayList<>();
    Date date = new Date();
    list.add("aaa");
    list.add("bbb");
    UserRecord userRecord = UserRecord.builder()
            .setDistinctId("123")
            .isLoginId(true)
            .addProperty("number1", 1234)
            .addProperty("date1", date)
            .addProperty("String1", "str")
            .addProperty("boolean1", false)
            .addProperty("list1", list)
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .build();
    sa.profileSetOnce(userRecord);

    assertNotNullProp();
    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }

  /**
   * 校验自定义属性格式是否正常
   */
  @Test
  public void testProfileIncrementEventBuilder() {
    Date date = new Date();
    List<String> list = new ArrayList<>();
    list.add("aaa");
    list.add("bbb");
    try {
      UserRecord userRecord = UserRecord.builder()
              .setDistinctId("123")
              .isLoginId(true)
              .addProperty("number1", 1234)
              .addProperty("$track_id", 111)
              .addProperty("$time", date)
              .build();

        sa.profileIncrement(userRecord);
        fail("profileIncrement should throw InvalidArgumentException");
    }catch (InvalidArgumentException e){
        assertEquals("The property value of PROFILE_INCREMENT should be a Number.The current type is class java.util.Date.", e.getMessage());
    }
  }

  @Test
  public void testProfileAppendByIdEventBuilder() throws InvalidArgumentException{
    Date date = new Date();
    List<String> list = new ArrayList<>();
    list.add("aaa");
    list.add("bbb");
    UserRecord userRecord = UserRecord.builder()
            .setDistinctId("123")
            .isLoginId(true)
            .addProperty("list1", list)
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .build();
    try {
      sa.profileAppend(userRecord);
      fail("profileAppend should throw InvalidArgumentException");
    }catch (InvalidArgumentException e){
      assertEquals("The property value of PROFILE_APPEND should be a List<String>.", e.getMessage());
    }

  }

  // profileUnsetById
  @Test
  public void testProfileUnsetByIdEventBuilder(){
    Date date = new Date();
    UserRecord userRecord = null;
    try {
      userRecord = UserRecord.builder()
              .setDistinctId("123")
              .isLoginId(true)
              .addProperty("$track_id", 111)
              .addProperty("$time", date)
              .build();
      sa.profileUnset(userRecord);
      fail("profileUnset should throw InvalidArgumentException");
    }catch (InvalidArgumentException e){
      assertEquals("The property value of $time should be true.", e.getMessage());
    }

  }

  // profileDeleteById

  @Test
  public void testProfileDeleteByIdEventBuilder() throws InvalidArgumentException{
    Date date = new Date();
    UserRecord userRecord = UserRecord.builder()
            .setDistinctId("123")
            .isLoginId(true)
            .addProperty("$track_id", 111)
            .addProperty("$time", date)
            .addProperty("abc", "acb")
            .build();
    sa.profileDelete(userRecord);

    assertNotNullProp();
    assertEquals(111, messageList.get(0).get("_track_id"));
    assertEquals(date.getTime(), messageList.get(0).get("time"));

    Map<String, Object> props = (Map<String, Object>)messageList.get(0).get("properties");
    assertFalse(props.containsKey("$track_id")); // properties 不包含 $track_id
  }
}
