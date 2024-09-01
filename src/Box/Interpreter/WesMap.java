package Box.Interpreter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WesMap<K, V> implements Map<K, V> {

	private Map<K,V> map = new HashMap<K, V>();
	
	
	
	
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return map.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return map.containsValue(arg0);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return map.entrySet();
	}

	@Override
	public V get(Object arg0) {
		Set<K> keySet = map.keySet();
		for (K k : keySet) {
			if(arg0.equals(k))
				return map.get(k);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return map.keySet();
	}

	@Override
	public V put(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return map.put(arg0, arg1);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
map.putAll(arg0);		
	}

	@Override
	public V remove(Object arg0) {
		// TODO Auto-generated method stub
		return map.remove(arg0);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return map.values();
	}

}
