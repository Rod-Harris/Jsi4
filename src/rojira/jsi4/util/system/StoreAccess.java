package rojira.jsi4.util.system;

import java.util.*;

public interface StoreAccess
{
	public void put( String type, String uuid, Map<String, byte[]> field_data );

	public Map<String, byte[]> get( String type, String uuid );

	public List<Map<String, byte[]>> get_all( String type, String condition );
}
