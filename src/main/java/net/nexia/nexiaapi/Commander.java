package net.nexia.nexiaapi;

import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("unused")
public class Commander
{

    public static void CommandArguments(String baseCommandClass, String pkg, String permissionNode, CommandSender sender, Command cmd, String label, String[] args)
    {
        try (ScanResult scanResult = new ClassGraph().acceptPackages(pkg).scan())
        {
            ClassInfoList classes = scanResult.getAllClasses();
            Class[] argumentTypes = { CommandSender.class, Command.class, String.class, String[].class };

            for (ClassInfo c : classes)
            {
                String className = c.getSimpleName();

                if (c.getSimpleName().equals(baseCommandClass)) continue; //Skip the Base Commands class
                if (!args[0].equalsIgnoreCase(className)) continue;

                try
                {
                    //Check for permissions
                    if (!sender.hasPermission(permissionNode + "." + className.toLowerCase()))
                    {
                        sender.sendMessage(Processes.color("&cInsufficient Permission"));
                        return;
                    }

                    //Calls the class constructor
                    Class.forName("net.nexia.tradingcards.Commands.Give").getDeclaredConstructor(argumentTypes).newInstance(sender, cmd, label, args);
                    return;
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static List<String> CommandArgumentsAutocomplete(int argsLength, String baseCommandClass, String pkg, String[] args)
    {
        if (args.length != argsLength) return null;

        try (ScanResult scanResult = new ClassGraph().acceptPackages(pkg).scan())
        {
            ClassInfoList classes = scanResult.getAllClasses();

            List<String> arguments = Lists.newArrayList(); //Arguments List
            List<String> list = Lists.newArrayList(); //Final List

            for (ClassInfo c : classes)
            {
                if (c.getSimpleName().equals(baseCommandClass)) continue; //Skip the Base Commands class
                arguments.add(c.getSimpleName().toLowerCase());
            }

            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) list.add(a);

            return list;
        }
    }

}
