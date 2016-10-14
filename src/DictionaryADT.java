
public interface DictionaryADT 
{
    public int insert (ConfigData pair) throws DictionaryException;

    public void remove (String config) throws DictionaryException;

    public int find (String config);

}