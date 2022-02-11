package AZ;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Dictionary
{
    private final LinkedHashMap<SocketAddress, Client> soc2cli = new LinkedHashMap<>();
    private final LinkedHashMap<Client, SocketAddress> cli2soc = new LinkedHashMap<>();
    //private HashMap<Tank, SocketAddress> tank2soc = new HashMap<>();
    
    public void put(SocketAddress sa, Client c)
    {
        soc2cli.put(sa, c);
        cli2soc.put(c, sa);
    }
    
    public Client get(SocketAddress sa)
    {
        return soc2cli.getOrDefault(sa, new Client());
    }
    
    public SocketAddress get(Client c)
    {
        return cli2soc.get(c);
    }
    
    public Client getOwner(Tank t)
    {
        return cli2soc.keySet().stream().filter(x -> x.t == t).findFirst().orElse(new Client());
    }
    
    public Set<Client> values()
    {
        return cli2soc.keySet();
    }
    
    public Set keySet()
    {
        return soc2cli.keySet();
    }
    
    public Set<Map.Entry<SocketAddress, Client>> entrySet()
    {
        return soc2cli.entrySet();
    }
    
    public boolean contains(SocketAddress sa)
    {
        return soc2cli.containsKey(sa);
    }
    
    public boolean contains(Client c)
    {
        return cli2soc.containsKey(c);
    }
    
    public void forEach(BiConsumer<? super SocketAddress, ? super Client> action)
    {
        soc2cli.forEach(action);
    }
    
    public Client remove(SocketAddress sa)
    {
        Client c = soc2cli.remove(sa);
        cli2soc.remove(c);
        return c;
    }
    
    public SocketAddress remove(Client c)
    {
        SocketAddress sa = cli2soc.remove(c);
        soc2cli.remove(sa);
        return sa;
    }
    
    public int size()
    {
        return cli2soc.size();
    }
    
    public boolean hasName(String name)
    {
        return cli2soc.keySet().stream().anyMatch(x -> x.name.equals(name));
    }
    
}
