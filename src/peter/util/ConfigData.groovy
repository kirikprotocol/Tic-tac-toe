package peter.util

/**
 * Created by Petr Matyukov on 03.05.2016.
 * Вспомогательный класс для чтения конфигов и сохранения HashTable в файл
 */
class ConfigData {
    private static String configPathParam = "store_dir"
    private static Hashtable hash = new Hashtable()
    static def setConfigPathParam(String path) {configPathParam = path}
    static def String getConfigPathParam() {return configPathParam}
    static def ConfigObject getConfig(String filename) {
        String configFilePath = System.properties[configPathParam]
        File configFile = new File(configFilePath, filename)
        if(hash.containsKey(filename)) {
            long lastModified = hash.get(filename + ".last_modified")
            if(lastModified == configFile.lastModified())
            return hash.get(filename)
        }
        ConfigObject config = new ConfigSlurper().parse(configFile.getText("UTF-8"))
        hash.put(filename, config)
        long lastModified = configFile.lastModified()
        hash.put(filename + ".last_modified", lastModified)
        return config
    }
    static def Hashtable getAt(String filename) {
        if(hash.containsKey(filename)) {
            return hash.get(filename)
        }
        else {
            String configFilePath = System.properties[configPathParam]
            File dataFile = new File(configFilePath, filename)
            Hashtable data = null
            try {
                ObjectInputStream objectInputStream = dataFile.newObjectInputStream()
                data = (Hashtable)objectInputStream.readObject()
                objectInputStream.close()
            }
            catch(IOException ex) {
                data = new Hashtable()
                try {
                    def outputStream = new FileOutputStream(dataFile)
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
                    objectOutputStream.writeObject(data)
                    objectOutputStream.close()
                }
                catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
            hash.put(filename, data)
            return data
        }
    }
    static def save(String filename){
        if(hash.containsKey(filename)) {
            String configFilePath = System.properties[configPathParam]
            File dataFile = new File(configFilePath, filename)
            try {
                def outputStream = new FileOutputStream(dataFile)
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
                objectOutputStream.writeObject(hash[filename])
                objectOutputStream.close()
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
        }
    }
}
