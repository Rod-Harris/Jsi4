package rojira.jsi4.util.system;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibDevel.*;
import static rojira.jsi4.LibSystem.*;
import java.util.*;

public class Table
{
	LinkedHashMap<String,Column> columns = new LinkedHashMap<String,Column>();

	ArrayList<Object[]> rows = new ArrayList<Object[]>();

	boolean type_safe = true;

	public int num_rows()
	{
		return rows.size();
	}

	public <T> void add_column( String name, Class<T> type )
	{
		require( rows.size() == 0 );

		columns.put( name, new Column( name, type, this ) );
	}


	public void add_row( Object... data )
	{
		EXPECT( data != null );

		EXPECT( data.length == columns.size() );

		if( rows.size() == 0 )
		{
			reindex_columns();
		}

		if( type_safe )
		{
			for( Column column : columns.values() )
			{
				int i = column.idx;

				cverbose.println( "Checking data for column %d is a %s", i, column.type );

				if( data[ i ] == null )
				{
					cverbose.println( "data is null - continuing" );

					continue;
				}

				cverbose.println( "data is of type %s", data[ i ].getClass() );

				EXPECT( column.type.isAssignableFrom( data[ i ].getClass() ) );
			}
		}

		rows.add( data );
	}


	public Object get( int row_idx, String col_name )
	{
		int col_idx = col( col_name ).idx;

		return get( row_idx, col_idx );
	}


	public Object get( int row_idx, int col_idx )
	{
		EXPECT( row_idx >= 0 );

		EXPECT( row_idx < rows.size() );

		require( col_idx >= 0 );

		require( col_idx < columns.size() );

		Object[] row_data = rows.get( row_idx );

		require( row_data.length == columns.size() );

		Object value = row_data[ col_idx ];

		cverbose.println( "table[ %d ][ %d ] = '%s'", row_idx, col_idx, value );

		return value;
	}


	void reindex_columns()
	{
		EXPECT( rows.size() == 0 );

		int idx = 0;

		for( String col_name : columns.keySet() )
		{
			columns.get( col_name ).idx = idx;

			idx ++;
		}
	}


	Column col( String name )
	{
		EXPECT( columns.containsKey( name ) );

		return columns.get( name );
	}


	public void dump_info()
	{
		cverbose.println( get_state( this ) );

		for( Object row : rows )
		{
			cverbose.println( get_state( row ) );
		}
	}


	public ArrayList<Object[]> rows()
	{
		return this.rows;
	}


}

class Column
{
	int idx;

	String name;

	Class type;

	Table table;

	Column( String name, Class type, Table table )
	{
		this.name = name;

		this.type = type;

		this.table = table;
	}

	public Object get( int row_idx )
	{
		return table.get( row_idx, idx );
	}
}
