package me.trouper.sentinel.server;

import io.papermc.paper.registry.RegistryAccess;
import me.trouper.sentinel.Sentinel;
import me.trouper.sentinel.data.IO;
import me.trouper.sentinel.data.config.lang.LanguageFile;
import me.trouper.sentinel.utils.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

public interface Main {
    Main main = new Main() {};
  
    Random random = new Random();

    default RegistryAccess getRegistryAccess() {
      return RegistryAccess.registryAccess();
    }

    default Sentinel getPlugin() {
        return Sentinel.getInstance();
    }
    
    default Logger getLogger() {
        return getPlugin().getLogger();
    }
    
    default Director dir() {
        return getPlugin().getDirector();
    }
    
    default IO io() {
        return dir().io;
    };
    
    default LanguageFile lang() {
        return io().lang;
    }

    default void infoAny(Audience player, String message, Object... args) {
        Text.messageAny(Text.Pallet.INFO, player, message, args);
    }

    default void errorAny(Audience player, String message, Object... args) {
        Text.messageAny(Text.Pallet.ERROR,player, message, args);
    }

    default void warningAny(Audience player, String message, Object... args) {
        Text.messageAny(Text.Pallet.WARNING, player, message, args);
    }

    default void successAny(Audience player, String message, Object... args) {
        Text.messageAny(Text.Pallet.SUCCESS, player, message, args);
    }

    default void messageAny(Audience player, String message, Object... args) {
        Text.messageAny(Text.Pallet.NEUTRAL, player, message, args);
    }
    
    default void info(Audience player, Component message, Component... args) {
        Text.message(Text.Pallet.INFO, player, message, args);
    }
    
    default void error(Audience player, Component message, Component... args) {
        Text.message(Text.Pallet.ERROR,player, message, args);
    }
    
    default void warning(Audience player, Component message, Component... args) {
        Text.message(Text.Pallet.WARNING, player, message, args);
    }

    default void success(Audience player, Component message, Component... args) {
        Text.message(Text.Pallet.SUCCESS, player, message, args);
    }

    default void message(Audience player, Component message, Component... args) {
        Text.message(Text.Pallet.NEUTRAL, player, message, args);
    }

    default void checkPre(boolean check, String msg, Object... args) {
        if (!check) {
            throw new IllegalArgumentException(msg.formatted(args));
        }
    }

    default void checkPre(BooleanSupplier check, String msg, Object... args) {
        checkPre(check.getAsBoolean(), msg, args);
    }
}
