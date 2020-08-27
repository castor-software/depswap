package se.kth.assertteam.depanalyzer;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibrariesUsage {
	int nbElements = 0;
	Set<String> packagesToLookFor;

	//Lib id -> package -> Member -> <Usages, From class>
	public Map<String, Map<String, Usage>> librariesPackagesMembersUsage = new HashMap<>();

	public boolean matchesKnownPrefix(String targetPackageName) {
		for(String prefix: packagesToLookFor) {
			if(targetPackageName.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}


	public LibrariesUsage(Set<String> packagesToLookFor) {
		this.packagesToLookFor = packagesToLookFor;
	}

	static String separator = ".";

	public void insertIfPartOfPackages(String owner, String targetMember, String fromClass) {
		int separatorIndex = owner.lastIndexOf('/');
		if(separatorIndex < 0) return;
		String targetPackageName = owner.substring(0, separatorIndex);
		if(matchesKnownPrefix(targetPackageName)) {
			String apiMember = owner.substring(separatorIndex + 1) + (targetMember.equals("") ? "" : (separator + targetMember));

			Map<String, Usage> membersUsage = librariesPackagesMembersUsage.computeIfAbsent(targetPackageName, pn -> new HashMap<>());
			Usage usage = membersUsage.computeIfAbsent(apiMember, m -> new Usage());
			usage.nb++;
			usage.from.add(fromClass);
			membersUsage.put(apiMember, usage);
		}
	}

	public int getQuerySize() {
		return nbElements;
	}

	public class Usage {
		public int nb = 0;
		public Set<String> from = new HashSet<>();

		@Override
		public String toString() {
			return "(" + nb + ", " + from.size() +")";
		}
		public String toJSONString() {
			return "{\"nb\":" + nb + ",\"from\":" + from.size() +"}";
		}
	}

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"static-usages\":{");
		boolean isFirst = true;
		for(String p: librariesPackagesMembersUsage.keySet()) {
			if(isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append("\"" + p + "\"");
			sb.append(":");
			sb.append("{");
			Map<String, Usage> membersUsages = librariesPackagesMembersUsage.get(p);
			boolean isFirst2 = true;
			for(String m : membersUsages.keySet()) {
				if(isFirst2) {
					isFirst2 = false;
				} else {
					sb.append(",");
				}
				sb.append("\"" + m + "\"");
				sb.append(":");
				sb.append(membersUsages.get(m).toJSONString());
			}
			sb.append("}");
		}
		sb.append("}}");
		return sb.toString();
	}
}
