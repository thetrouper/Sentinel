package io.github.thetrouper.sentinel.commands;

import io.github.thetrouper.sentinel.Sentinel;
import io.github.thetrouper.sentinel.server.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CustomCommand implements TabExecutor {

    protected static final Sentinel system = Sentinel.getInstance();
    private final String name;
    private boolean printStacktrace;

    public CustomCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrintStacktrace(boolean printStacktrace) {
        this.printStacktrace = printStacktrace;
    }

    public boolean canPrintStacktrace() {
        return printStacktrace;
    }

    public abstract void dispatchCommand(CommandSender sender, Command command, String label, String[] args);
    public abstract void registerCompletions(CompletionBuilder builder);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            dispatchCommand(sender, command, label, args);
        }
        catch (Exception ex) {
            String msg = ex.getMessage();

            if (ex instanceof IndexOutOfBoundsException)
                msg = "command incomplete";
            else if (ex instanceof NullPointerException)
                msg = "command contains a null value";

            sender.sendMessage(TextUtils.prefix("§4Command Error: §cUnknown or incomplete command!"));
            sender.sendMessage(TextUtils.prefix("§cCaused by: §8§o(" + ex.getClass().getSimpleName() + ") §7" + msg));
            sender.sendMessage(TextUtils.prefix("§cCorrect Usage: §7" + command.getUsage()));

            if (printStacktrace) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        CompletionBuilder builder = new CompletionBuilder(sender, command, label, args);
        registerCompletions(builder);
        return builder.build();
    }

    public CustomCommand register() {
        return register(this);
    }

    public static CustomCommand register(CustomCommand command) {
        system.getCommand(command.name).setExecutor(command);
        system.getCommand(command.name).setTabCompleter(command);
        return command;
    }

    public static class CompletionBuilder {

        public final CommandSender sender;
        public final Command command;
        public final String label;
        public final String[] args;
        private final Map<Integer, List<String>> entries;

        public CompletionBuilder(CommandSender sender, Command command, String label, String[] args) {
            this.sender = sender;
            this.command = command;
            this.label = label;
            this.args = args;
            this.entries = new HashMap<>();
        }

        public CompletionBuilder addCompletion(int index, Predicate<CompletionBuilder> condition, Iterable<String> args) {
            addCompletion(index, condition.test(this), args);
            return this;
        }

        public CompletionBuilder addCompletion(int index, Predicate<CompletionBuilder> condition, String... args) {
            addCompletion(index, condition.test(this), args);
            return this;
        }

        public CompletionBuilder addCompletion(int index, Predicate<CompletionBuilder> condition, List<String> args) {
            addCompletion(index, condition.test(this), args);
            return this;
        }

        public CompletionBuilder addCompletion(int index, boolean condition, Iterable<String> args) {
            if (condition) {
                addCompletion(index, args);
            }
            return this;
        }

        public CompletionBuilder addCompletion(int index, boolean condition, String... args) {
            if (condition) {
                addCompletion(index, args);
            }
            return this;
        }

        public CompletionBuilder addCompletion(int index, boolean condition, List<String> args) {
            if (condition) {
                addCompletion(index, args);
            }
            return this;
        }

        public CompletionBuilder addCompletion(int index, Iterable<String> args) {
            List<String> list = new ArrayList<>();
            args.forEach(list::add);
            addCompletion(index, list);
            return this;
        }

        public CompletionBuilder addCompletion(int index, String... args) {
            addCompletion(index, Arrays.asList(args));
            return this;
        }

        public CompletionBuilder addCompletion(int index, List<String> args) {
            entries.put(index, args);
            return this;
        }

        public void removeCompletion(int index) {
            entries.remove(index);
        }

        public <I> List<String> convertLists(Collection<I> input, Function<I, String> conversion) {
            List<String> list = new ArrayList<>();
            for (I i : input) {
                list.add(conversion.apply(i));
            }
            return list;
        }

        public List<String> build() {
            return entries.getOrDefault(args.length, new ArrayList<>()).stream()
                    .filter(s -> s.toLowerCase().contains(args[args.length - 1].toLowerCase()))
                    .toList();
        }
    }
}
