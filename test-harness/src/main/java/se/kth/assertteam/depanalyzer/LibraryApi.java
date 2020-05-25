package se.kth.assertteam.depanalyzer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibraryApi {
	//insertType
	//insertMethod
	//insertField



	//Package -> Class -> member
	public Map<String,Map<String,Set<Map.Entry<String,Boolean>>>> apiMembers;

	int libraryId;

	public LibraryApi(int libraryId) {
		apiMembers = new HashMap<>();
		this.libraryId = libraryId;
	}

	static String separator = ".";

	public void insert(String owner, String targetMember, boolean isPublic) {
		int separatorIndex = owner.lastIndexOf('/');
		String targetPackageName = owner.substring(0, separatorIndex);
		String className = owner.substring(separatorIndex+1);
		String member = targetMember.equals("") ? "NULL" : targetMember;
		insert(targetPackageName, className, member, isPublic);
	}

	public void insert(String packageName, String className, String member, boolean isPublic) {
		Map<String,Set<Map.Entry<String,Boolean>>> packageMembers = apiMembers.computeIfAbsent(packageName, p -> new HashMap<>());
		Set<Map.Entry<String,Boolean>> classMembers = packageMembers.computeIfAbsent(className, c -> new HashSet<>());
		classMembers.add(new HashMap.SimpleEntry<>(member,isPublic));
		packageMembers.put(className,classMembers);
		apiMembers.put(packageName,packageMembers);
	}
}