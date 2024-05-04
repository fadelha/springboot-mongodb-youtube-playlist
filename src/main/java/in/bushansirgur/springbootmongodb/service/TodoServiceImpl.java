package in.bushansirgur.springbootmongodb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.bushansirgur.springbootmongodb.exception.TodoCollectionException;
import in.bushansirgur.springbootmongodb.model.TodoDTO;
import in.bushansirgur.springbootmongodb.repository.TodoRepository;
import jakarta.validation.ConstraintViolationException;

@Service
public class TodoServiceImpl implements TodoService {

	@Autowired
	private TodoRepository todoRepo;
	
	@Override
	public void createTodo(TodoDTO todo) throws ConstraintViolationException,TodoCollectionException {
		
		Optional <TodoDTO> todoOptional=todoRepo.findByTodo(todo.getTodo());
		if(todoOptional.isPresent())
		{ 
			throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
		}
		else
		{
			todo.setCreatedAt(new Date(System.currentTimeMillis()));
			todoRepo.save(todo);
			// ho satren keno bl postmapping bi alb try
		}
	}

	@Override
	public List<TodoDTO> getAllTodos() {
		List<TodoDTO> todos=todoRepo.findAll();
		if(todos.size()>0)
		{
			return todos;
		}
		else {
			return new ArrayList<TodoDTO>();
		}
	}

	@Override
	public TodoDTO getSingleTodo(String id) throws TodoCollectionException {
		Optional<TodoDTO> optionalTodo=todoRepo.findById(id);
		if(!optionalTodo.isPresent())
		{
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
		}
		else
		{
			return optionalTodo.get();
		}
	}

	@Override
	public void updateTodo(String id, TodoDTO todo) throws TodoCollectionException {
		Optional<TodoDTO> todoWithId=todoRepo.findById(id);
		Optional<TodoDTO> todoWithSameName= todoRepo.findByTodo(todo.getTodo());
		if(todoWithId.isPresent())
		{
			//hay eno deja mawjude bas bi 8er id ya3ne duplicate bl DB so kermel et2kd eno ma fi 8yra 7asab id w bas
			//ya3ne eza fi 8ayra bas 8er id ya3ne user 3emil create la nfs lwe7de marten eza badak 
			//fa se3eta b2eello deja mawjude eno fi 8ayra 8er id eza badak
			//3m y3ml update la todo mawjude bi todo deja mawjude fa hek bisir fi duplicate fa kermel ma ysir 
			//duplication hek mmn3ml
			// leh 7atyna !  laan huwe eza nfs id w nfs name w bado y8yir shi tene fiya 3ade besm7lo
			if(todoWithSameName.isPresent() && !todoWithSameName.get().getId().equals(id))
			{
				throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
			}
			
			
			
			TodoDTO todoToUpdate=todoWithId.get();
			todoToUpdate.setTodo(todo.getTodo());
			todoToUpdate.setDescription(todo.getDescription());
			todoToUpdate.setCompleted(todo.getCompleted());
			todoToUpdate.setUpdatedAt(new Date(System.currentTimeMillis()));
			todoRepo.save(todoToUpdate); //ya3ne save bl database
		}
		else
		{
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
		}
		
	}

	@Override
	public void deleteTodoById(String id) throws TodoCollectionException {
		Optional<TodoDTO> todoOptional= todoRepo.findById(id);
		if(!todoOptional.isPresent())
		{
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
		}
		else
		{
			todoRepo.deleteById(id);
		}
		
	}

}
