package com.example.CRMTicketing.dao;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.service.AgentService;
import com.example.CRMTicketing.service.TicketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Controller
public class Fetcher<T>{
    private final LinkedHashMap<String,T> map;
    public T getById(String id){
        return map.get(id);
    }
    public Collection<T> get(){
        return map.values();
    }
    public boolean contains(String id){
        return map.containsKey(id);
    }
    public void save(String id,T obj){
        map.put(id,obj);
    }

    public void saveAll(List<T> items) {
        for(T obj:items){
            if(obj instanceof Ticket)
                map.put(((Ticket) obj).getId()+"-TKT",obj);
            else if(obj instanceof Agent)
                map.put(((Agent) obj).getId()+"-AGT",obj);
        }
    }
}
