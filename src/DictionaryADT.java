
public interface DictionaryADT 
{
    public int insert (DictEntry pair) throws DictionaryException;

    public void remove (String config) throws DictionaryException;

    public int find (String config);

}