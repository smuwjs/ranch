package org.lpw.ranch.classify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.ranch.recycle.Recycle;
import org.lpw.ranch.recycle.RecycleHelper;
import org.lpw.ranch.util.Pagination;
import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.scheduler.DateJob;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Service(ClassifyModel.NAME + ".service")
public class ClassifyServiceImpl implements ClassifyService, DateJob {
    private static final String CACHE_RANDOM = ClassifyModel.NAME + ".service.random";
    private static final String CACHE_TREE = ClassifyModel.NAME + ".service.tree:";
    private static final String CACHE_JSON = ClassifyModel.NAME + ".service.json:";
    private static final String CACHE_GET = ClassifyModel.NAME + ".service.get:";
    private static final String CACHE_LIST = ClassifyModel.NAME + ".service.list:";

    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Generator generator;
    @Inject
    private Json json;
    @Inject
    private Pagination pagination;
    @Inject
    private RecycleHelper recycleHelper;
    @Inject
    private ClassifyDao classifyDao;
    private Set<String> ignores;

    @Override
    public JSONObject query(String code, String key, String value, String name) {
        return toJson(validator.isEmpty(code) ? classifyDao.query(pagination.getPageSize(), pagination.getPageNum())
                : classifyDao.query(code, key, value, name, pagination.getPageSize(), pagination.getPageNum()), Recycle.No);
    }

    @Override
    public JSONArray tree(String code) {
        String cacheKey = CACHE_TREE + getRandom() + code;
        JSONArray array = cache.get(cacheKey);
        if (array == null) {
            array = new JSONArray();
            for (ClassifyModel classify : classifyDao.query(code, null, null, null, 0, 0).getList()) {
                JSONObject object = getJson(classify.getId(), classify, Recycle.No);
                JSONObject parent = findParent(array, classify.getCode());
                if (parent == null)
                    array.add(object);
                else
                    json.addAsArray(parent, "children", object);
            }
            cache.put(cacheKey, array, false);
        }

        return array;
    }

    private JSONObject findParent(JSONArray array, String code) {
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            String string = object.getString("code");
            if (code.startsWith(string) && !code.equals(string)) {
                if (object.containsKey("children")) {
                    JSONObject child = findParent(object.getJSONArray("children"), code);
                    if (child != null)
                        return child;
                }

                return object;
            }
        }

        return null;
    }

    @Override
    public JSONObject get(String[] ids) {
        String cacheKey = CACHE_GET + converter.toString(ids);
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            object = new JSONObject();
            for (String id : ids) {
                JSONObject json = getJson(id, null, Recycle.No);
                if (json.isEmpty())
                    continue;

                object.put(id, json);
                if (!json.containsKey("links"))
                    continue;

                Object links = json.get("links");
                if (links instanceof JSONArray)
                    object.putAll(get(((JSONArray) links).toArray(new String[0])));
            }
            cache.put(cacheKey, object, false);
        }

        return object;
    }

    @Override
    public JSONObject find(String code, String value) {
        if (validator.isEmpty(code) || validator.isEmpty(value))
            return new JSONObject();

        String cacheKey = CACHE_GET + getRandom() + code + value;
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            ClassifyModel classify = classifyDao.findByCodeValue(code, value);
            if (classify == null)
                return new JSONObject();

            cache.put(cacheKey, object = getJson(classify.getId(), classify, Recycle.No), false);
        }

        return object;
    }

    @Override
    public JSONArray list(String code, String key, String name) {
        String cacheKey = CACHE_LIST + getRandom() + code + key + name;
        JSONArray array = cache.get(cacheKey);
        if (array == null) {
            array = new JSONArray();
            for (ClassifyModel classify : classifyDao.query(code, null, null, null, 0, 0).getList())
                if (contains(classify, key, name))
                    array.add(getJson(classify.getId(), classify, Recycle.No));
            cache.put(cacheKey, array, false);
        }

        return array;
    }

    private boolean contains(ClassifyModel classify, String key, String name) {
        boolean[] empty = new boolean[]{validator.isEmpty(key), validator.isEmpty(name), validator.isEmpty(classify.getKey())};
        if (empty[0] && empty[1])
            return true;

        if (empty[0])
            return classify.getName().contains(name);

        if (empty[2])
            return false;

        if (empty[1])
            return classify.getKey().contains(key);

        return classify.getKey().contains(key) && classify.getName().contains(name);
    }

    @Override
    public JSONObject create(Map<String, String> map) {
        ClassifyModel classify = new ClassifyModel();
        classify.setCode(map.get("code"));
        classify.setKey(map.get("key"));
        classify.setValue(map.get("value"));
        classify.setName(map.get("name"));

        return save(classify, new JSONObject(), map);
    }

    @Override
    public JSONObject modify(ClassifyModel model, Map<String, String> map) {
        ClassifyModel classify = findById(model.getId());
        if (!validator.isEmpty(model.getCode()))
            classify.setCode(model.getCode());
        if (!validator.isEmpty(model.getKey()))
            classify.setKey(model.getKey());
        if (!validator.isEmpty(model.getValue()))
            classify.setValue(model.getValue());
        if (!validator.isEmpty(model.getName()))
            classify.setName(model.getName());

        return save(classify, validator.isEmpty(classify.getJson()) ? new JSONObject() : JSON.parseObject(classify.getJson()), map);
    }

    private JSONObject save(ClassifyModel classify, JSONObject json, Map<String, String> map) {
        map.forEach((key, value) -> {
            if (ignore(key))
                return;

            if (key.charAt(0) == '-')
                json.remove(key.substring(1));
            else
                json.put(key, value);
        });
        classify.setJson(json.toJSONString());
        classifyDao.save(classify);

        return getJson(classify.getId(), classify, Recycle.No);
    }

    private boolean ignore(String key) {
        if (ignores == null) {
            ignores = new HashSet<>();
            ignores.add("id");
            ignores.add("code");
            ignores.add("key");
            ignores.add("value");
            ignores.add("name");
            ignores.add("sign-time");
            ignores.add("sign");
        }

        return ignores.contains(key);
    }

    private JSONObject getJson(String id, ClassifyModel classify, Recycle recycle) {
        String cacheKey = CACHE_JSON + getRandom() + id;
        JSONObject object = classify == null ? cache.get(cacheKey) : null;
        if (object == null) {
            object = new JSONObject();
            if (classify == null)
                classify = findById(id);
            if (classify == null || classify.getRecycle() != recycle.getValue()) {
                cache.put(cacheKey, object, false);

                return object;
            }

            object.put("id", classify.getId());
            object.put("code", classify.getCode());
            object.put("key", classify.getKey());
            object.put("value", classify.getValue());
            object.put("name", classify.getName());
            if (!validator.isEmpty(classify.getJson()))
                object.putAll(json.toObject(classify.getJson()));
            cache.put(cacheKey, object, false);
        }

        return object;
    }

    @Override
    public void delete(String id) {
        recycleHelper.delete(ClassifyModel.class, id);
    }

    @Override
    public JSONObject recycle() {
        return recycleHelper.recycle(ClassifyModel.class);
    }

    private JSONObject toJson(PageList<ClassifyModel> pl, Recycle recycle) {
        JSONObject object = pl.toJson(false);
        JSONArray array = new JSONArray();
        pl.getList().forEach(classify -> array.add(getJson(classify.getId(), classify, recycle)));
        object.put("list", array);

        return object;
    }

    @Override
    public void restore(String id) {
        recycleHelper.restore(ClassifyModel.class, id);
    }

    @Override
    public void refresh() {
        resetRandom();
    }

    @Override
    public ClassifyModel findById(String id) {
        return classifyDao.findById(id);
    }

    private String getRandom() {
        String random = cache.get(CACHE_RANDOM);

        return validator.isEmpty(random) ? resetRandom() : random;
    }

    private String resetRandom() {
        String random = generator.random(32);
        cache.put(CACHE_RANDOM, random, true);

        return random;
    }

    @Override
    public void executeDateJob() {
        resetRandom();
    }
}
